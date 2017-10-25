package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.TypeConverters
import com.example.liudong.archdemo.db.GithubTypeConverters

@Entity(primaryKeys = arrayOf("query"))
@TypeConverters(GithubTypeConverters::class)
data class RepoSearchResult @Ignore constructor(
        var query: String? = "",
        var repoIds: List<Int> = mutableListOf(),
        var totalCount: Int = 0,
        var next: Int = 0
) {
    constructor() : this("")
}
