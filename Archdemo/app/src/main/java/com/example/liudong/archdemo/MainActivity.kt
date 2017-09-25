package com.example.liudong.archdemo

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.liudong.archdemo.db.GithubDb


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(application, GithubDb::class.java, "github.db")
                .build()
        db.close()
    }
}
