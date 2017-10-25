package com.example.liudong.archdemo.di

import com.example.liudong.archdemo.ui.repo.RepoFragment
import com.example.liudong.archdemo.ui.search.SearchFragment
import com.example.liudong.archdemo.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}