package com.example.liudong.archdemo.di

import android.app.Application
import android.arch.persistence.room.Room
import com.example.liudong.archdemo.api.GithubService
import com.example.liudong.archdemo.db.GithubDb
import com.example.liudong.archdemo.db.RepoDao
import com.example.liudong.archdemo.db.UserDao
import com.example.liudong.archdemo.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
internal class AppModule {
    @Singleton
    @Provides
    fun provideGithubService(): GithubService {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        return Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(GithubService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): GithubDb {
        return Room.databaseBuilder(app, GithubDb::class.java, "github.db").build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GithubDb): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }
}