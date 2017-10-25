package com.example.liudong.archdemo.di

import com.example.liudong.archdemo.ui.repo.RepoViewModel
import com.example.liudong.archdemo.ui.search.SearchViewModel
import com.example.liudong.archdemo.ui.user.UserViewModel
import dagger.Subcomponent

/**
 * A sub component to create ViewModels. It is called by the
 * [com.example.liudong.archdemo.viewmodel.GithubViewModelFactory]. Using this component allows
 * ViewModels to define [javax.inject.Inject] constructors.
 */
@Subcomponent
interface ViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewModelSubComponent
    }

    fun userViewModel(): UserViewModel
    fun searchViewModel(): SearchViewModel
    fun repoViewModel(): RepoViewModel
}