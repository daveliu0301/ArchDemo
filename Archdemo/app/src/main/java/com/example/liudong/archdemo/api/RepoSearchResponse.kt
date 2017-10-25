package com.example.liudong.archdemo.api

import com.example.liudong.archdemo.vo.Repo
import com.google.gson.annotations.SerializedName

/**
 * POJO to hold repo search responses. This is different from the Entity in the database because
 * we are keeping a search result in 1 row and denormalizing list of results into a single column.
 */
class RepoSearchResponse {
    @SerializedName("total_count")
    var total: Int = 0
    @SerializedName("items")
    var items: List<Repo> = listOf()
    var nextPage: Int = 0

    val repoIds: List<Int>
        get() = items.map { it.id }
}
