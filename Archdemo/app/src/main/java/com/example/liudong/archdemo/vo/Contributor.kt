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
class Contributor @Ignore constructor(@SerializedName("login")
                                      var login: String,
                                      @SerializedName("contributions")
                                      var contributions: Int,
                                      @SerializedName("avatar_url")
                                      var avatarUrl: String) {
    constructor() : this("", 0, "")

    var repoName: String? = null

    var repoOwner: String? = null
}
