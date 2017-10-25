package com.example.liudong.archdemo.repository

import android.arch.lifecycle.LiveData
import com.example.liudong.archdemo.AppExecutors
import com.example.liudong.archdemo.api.ApiResponse
import com.example.liudong.archdemo.api.GithubService
import com.example.liudong.archdemo.db.UserDao
import com.example.liudong.archdemo.vo.Resource
import com.example.liudong.archdemo.vo.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class UserRepository @Inject
internal constructor(private val appExecutors: AppExecutors,
                     private val userDao: UserDao,
                     private val githubService: GithubService) {

    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User?) {
                item?.let { userDao.insert(it) }
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.findByLogin(login)
            }

            override fun createCall(): LiveData<ApiResponse<User>> {
                return githubService.getUser(login)
            }
        }.asLiveData()
    }
}
