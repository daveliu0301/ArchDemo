package com.example.liudong.archdemo.db

import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.util.StringUtil

class GithubTypeConverters {
    @TypeConverter
    fun stringToIntList(data: String?): List<Int> {
        return StringUtil.splitToIntList(data) ?: emptyList()
    }

    @TypeConverter
    fun intListToString(ints: List<Int>): String {
        return StringUtil.joinIntoString(ints) ?: ""
    }
}