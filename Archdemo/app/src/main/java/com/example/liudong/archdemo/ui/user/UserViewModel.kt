package com.example.liudong.archdemo.ui.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import com.example.liudong.archdemo.repository.RepoRepository
import com.example.liudong.archdemo.repository.UserRepository
import com.example.liudong.archdemo.util.AbsentLiveData
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import com.example.liudong.archdemo.vo.User
import javax.inject.Inject

class UserViewModel @Inject constructor(
        userRepository: UserRepository,
        repoRepository: RepoRepository
) : ViewModel() {
    @VisibleForTesting
    val login = MutableLiveData<String>()
    val repositories: LiveData<Resource<List<Repo>>>
    val user: LiveData<Resource<User>>

    init {
        user = Transformations.switchMap(login) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUser(it)
            }
        }
        repositories = Transformations.switchMap(login) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repoRepository.loadRepos(login)
            }
        }
    }

    fun setLogin(login: String) {
        if (this.login.value == login) {
            return
        }
        this.login.value = login
    }

    fun retry() {
        if (this.login.value != null) {
            this.login.value = this.login.value
        }
    }
}