package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = arrayOf("login"))
data class User @Ignore constructor(
        @SerializedName("login")
        var login: String? = "",
        @SerializedName("avatar_url")
        var avatarUrl: String? = "",
        @SerializedName("name")
        var name: String? = "",
        @SerializedName("company")
        var company: String? = "",
        @SerializedName("repos_url")
        var reposUrl: String? = "",
        @SerializedName("blog")
        var blog: String? = ""
) {
    constructor() : this("")
}
