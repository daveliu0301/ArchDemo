package com.example.liudong.archdemo.api

import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T>
</T> */
class ApiResponse<T> {
    val code: Int
    val body: T?
    val errorMessage: String
    val links: Map<String, String>

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message ?: ""
        links = emptyMap()
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = ""
        } else {
            var message = ""
            try {
                message = response.errorBody()?.string() ?: ""
            } catch (ignored: IOException) {
                ignored.printStackTrace()
            }

            if (message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
        val linkHeader = response.headers().get("link")
        if (linkHeader == null) {
            links = emptyMap()
        } else {
            links = mutableMapOf()
            val matcher = LINK_PATTERN.matcher(linkHeader)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links.put(matcher.group(2), matcher.group(1))
                }
            }
        }
    }

    val isSuccessful: Boolean
        get() = code in 200..299

    val nextPage: Int
        get() {
            val next = links[NEXT_LINK] ?: return 0
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                return 0
            }
            return try {
                matcher.group(1).toInt()
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
                0
            }

        }

    companion object {
        private val LINK_PATTERN = Pattern
                .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("page=(\\d)+")
        private val NEXT_LINK = "next"
    }
}
