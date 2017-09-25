package com.example.liudong.archdemo.vo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import com.google.gson.annotations.SerializedName

/**
 * Using name/owner_login as primary key instead of id since name/owner_login is always available
 * vs id is not.
 */
@Entity(indices = arrayOf(Index("id", "owner_login")),
        primaryKeys = arrayOf("name", "owner_login"))
class Repo @Ignore constructor(var id: Int,
                               @SerializedName("name")
                               var name: String,
                               @SerializedName("full_name")
                               var fullName: String,
                               @SerializedName("description")
                               var description: String,
                               @SerializedName("owner")
                               @Embedded(prefix = "owner_")
                               var owner: Owner?,
                               @SerializedName("stargazers_count")
                               var stars: Int) {
    constructor() : this(0, "", "", "", Owner("", ""), 0)

    companion object {
        val UNKNOWN_ID = -1
    }


}
