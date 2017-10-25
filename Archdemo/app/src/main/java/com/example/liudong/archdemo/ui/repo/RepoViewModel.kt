package com.example.liudong.archdemo.ui.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import com.example.liudong.archdemo.repository.RepoRepository
import com.example.liudong.archdemo.util.AbsentLiveData
import com.example.liudong.archdemo.vo.Contributor
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import timber.log.Timber
import javax.inject.Inject


class RepoViewModel @Inject constructor(
        repository: RepoRepository
) : ViewModel() {
    @VisibleForTesting
    private val repoId = MutableLiveData<RepoId>()
    val repo: LiveData<Resource<Repo>> = Transformations.switchMap(repoId, {
        Timber.d("Timber input.isEmpty: " + it.isEmpty)
        if (it.isEmpty) {
            AbsentLiveData.create()
        } else {
            repository.loadRepo(it.owner ?: "", it.name ?: "")
        }
    })
    val contributors: LiveData<Resource<List<Contributor>>> = Transformations.switchMap(repoId, {
        if (it.isEmpty) {
            AbsentLiveData.create()
        } else {
            repository.loadContributors(it.owner ?: "", it.name ?: "")
        }

    })

    fun retry() {
        val current = repoId.value
        if (current != null && !current.isEmpty) {
            repoId.value = current
        }
    }

    fun setId(owner: String, name: String) {
        val update = RepoId(owner, name)
        if (repoId.value == update) {
            return
        }
        repoId.value = update
        Timber.d("Timber repoId.value: " + repoId.value)
    }

    @VisibleForTesting
    data class RepoId(var owner: String?, var name: String?) {
        init {
            owner = owner?.trim { it <= ' ' }
            name = name?.trim { it <= ' ' }
        }

        val isEmpty: Boolean
            get() = owner?.isEmpty() ?: true || name?.isEmpty() ?: true
    }
}
