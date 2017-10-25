package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

data class Owner @Ignore constructor(
        @SerializedName("login")
        var login: String? = "",
        @SerializedName("url")
        var url: String? = ""
) {
    constructor() : this("")
}