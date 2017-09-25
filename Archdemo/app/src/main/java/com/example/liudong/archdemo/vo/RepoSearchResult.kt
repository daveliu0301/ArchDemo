package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.TypeConverters
import com.example.liudong.archdemo.db.GithubTypeConverters

@Entity(primaryKeys = arrayOf("query"))
@TypeConverters(GithubTypeConverters::class)
class RepoSearchResult @Ignore constructor(var query: String,
                                           var repoIds: List<Int>,
                                           var totalCount: Int,
                                           var next: Int) {
    constructor() : this("", mutableListOf(), 0, 0)
}
