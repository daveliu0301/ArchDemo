package com.example.liudong.archdemo.ui.common

import android.support.v4.app.FragmentManager
import com.example.liudong.archdemo.MainActivity
import com.example.liudong.archdemo.R
import com.example.liudong.archdemo.ui.repo.RepoFragment
import com.example.liudong.archdemo.ui.search.SearchFragment
import com.example.liudong.archdemo.ui.user.UserFragment
import timber.log.Timber
import javax.inject.Inject


/**
 * A utility class that handles navigation in [MainActivity].
 */
class NavigationController @Inject constructor(
        mainActivity: MainActivity
) {
    private val containerId: Int = R.id.container
    private val fragmentManager: FragmentManager = mainActivity.supportFragmentManager

    fun navigateToSearch() {

        Timber.d("Timber navigateToSearch: ")
        val searchFragment = SearchFragment()
        fragmentManager.beginTransaction()
                .replace(containerId, searchFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToRepo(owner: String, name: String) {
        Timber.d("Timber navigateToRepo: ")
        val fragment = RepoFragment.create(owner, name)
        val tag = "repo/$owner/$name"
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun navigateToUser(login: String) {
        Timber.d("Timber navigateToUser: ")
        val tag = "user" + "/" + login
        val userFragment = UserFragment.create(login)
        fragmentManager.beginTransaction()
                .replace(containerId, userFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}