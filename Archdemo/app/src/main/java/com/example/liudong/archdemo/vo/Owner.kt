package com.example.liudong.archdemo.vo

import com.google.gson.annotations.SerializedName

class Owner(@SerializedName("login")
            val login: String?,
            @SerializedName("url")
            val url: String?)