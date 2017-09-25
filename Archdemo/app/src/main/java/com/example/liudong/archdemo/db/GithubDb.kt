package com.example.liudong.archdemo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.example.liudong.archdemo.vo.Contributor
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.RepoSearchResult
import com.example.liudong.archdemo.vo.User

/**
 * Main database description.
 */
@Database(
        entities = arrayOf(
                User::class,
                Repo::class,
                Contributor::class,
                RepoSearchResult::class),
        version = 3)
abstract class GithubDb : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao
}
