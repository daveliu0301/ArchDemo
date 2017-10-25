package com.example.liudong.archdemo.repository

import android.arch.lifecycle.MutableLiveData
import com.example.liudong.archdemo.api.ApiResponse
import com.example.liudong.archdemo.api.GithubService
import com.example.liudong.archdemo.db.GithubDb
import com.example.liudong.archdemo.vo.RepoSearchResult
import com.example.liudong.archdemo.vo.Resource
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextSearchPageTask constructor(
        private val query: String,
        private val githubService: GithubService,
        private val db: GithubDb
) : Runnable {
    val liveData = MutableLiveData<Resource<Boolean>>()

    override fun run() {
        val current = db.repoDao().findSearchResult(query)
        if (current == null) {
            liveData.postValue(null)
            return
        }
        val nextPage = current.next
        if (nextPage == 0) {
            liveData.postValue(Resource.success(false))
            return
        }
        try {
            val response = githubService.searchRepos(query, nextPage).execute()
            val apiResponse = ApiResponse(response)
            if (apiResponse.isSuccessful) {
                // we merge all repo ids into 1 list so that it is easier to fetch the result list.
                val ids = mutableListOf<Int>()
                ids.addAll(current.repoIds)

                val repoIds = apiResponse.body?.repoIds
                if (repoIds != null) {
                    ids.addAll(apiResponse.body.repoIds)
                }
                val merged = apiResponse.body?.total?.let {
                    RepoSearchResult(query, ids, it, apiResponse.nextPage)
                }
                try {
                    db.beginTransaction()
                    merged?.let {
                        db.repoDao().insert(it)
                    }
                    apiResponse.body?.items?.let {
                        db.repoDao().insertRepos(it)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                liveData.postValue(Resource.success(true))
            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage, true))
            }
        } catch (e: IOException) {
            liveData.postValue(Resource.error(e.message ?: "", true))
        }

    }
}