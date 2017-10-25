package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = arrayOf("repoName", "repoOwner", "login"),
        foreignKeys = arrayOf(ForeignKey(
                entity = Repo::class,
                parentColumns = arrayOf("name", "owner_login"),
                childColumns = arrayOf("repoName", "repoOwner"),
                onUpdate = ForeignKey.CASCADE,
                deferred = true)))
data class Contributor @Ignore constructor(
        @SerializedName("login")
        var login: String? = "",
        @SerializedName("contributions")
        var contributions: Int = 0,
        @SerializedName("avatar_url")
        var avatarUrl: String? = ""
) {
    constructor() : this("")

    var repoName: String? = ""
    var repoOwner: String? = ""
}
