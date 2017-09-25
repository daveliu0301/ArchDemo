package com.example.liudong.archdemo.db

import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.util.StringUtil
import java.util.*

class GithubTypeConverters {
    @TypeConverter
    fun stringToIntList(data: String?): MutableList<Int>? {
        if (data == null) {
            return Collections.emptyList()
        }
        return StringUtil.splitToIntList(data)
    }

    @TypeConverter
    fun intListToString(ints: List<Int>): String? {
        return StringUtil.joinIntoString(ints)
    }
}