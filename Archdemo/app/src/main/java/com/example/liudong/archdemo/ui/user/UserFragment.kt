package com.example.liudong.archdemo.ui.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.liudong.archdemo.databinding.UserFragmentBinding
import com.example.liudong.archdemo.di.Injectable
import com.example.liudong.archdemo.ui.common.NavigationController
import com.example.liudong.archdemo.ui.common.RepoListAdapter
import com.example.liudong.archdemo.ui.common.RetryCallback
import com.example.liudong.archdemo.util.AutoClearedValue
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import com.example.liudong.archdemo.vo.User
import javax.inject.Inject

class UserFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigationController: NavigationController

    lateinit private var userViewModel: UserViewModel
    private lateinit var binding: AutoClearedValue<UserFragmentBinding>
    private lateinit var adapter: AutoClearedValue<RepoListAdapter>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = UserFragmentBinding.inflate(inflater)
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                userViewModel.retry()
            }
        }
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
        userViewModel.setLogin(arguments.getString(LOGIN_KEY))
        userViewModel.user.observe(this, Observer<Resource<User>> { userResource ->
            binding.value!!.user = userResource?.data
            binding.value!!.userResource = userResource
            // this is only necessary because espresso cannot read data binding callbacks.
            binding.value!!.executePendingBindings()
        })
        val rvAdapter = RepoListAdapter(false, {
            navigationController.navigateToRepo(it.owner?.login ?: "", it.name ?: "")
        })
        binding.value!!.repoList.adapter = rvAdapter
        this.adapter = AutoClearedValue(this, rvAdapter)
        initRepoList()
    }

    private fun initRepoList() {
        userViewModel.repositories.observe(this, Observer<Resource<List<Repo>>> {
            // no null checks for adapter.get() since LiveData guarantees that we'll not receive
            // the event if fragment is now show.
            adapter.value!!.replace(it?.data)
        })
    }

    companion object {
        private val LOGIN_KEY = "login"

        fun create(login: String): UserFragment {
            val userFragment = UserFragment()
            val bundle = Bundle()
            bundle.putString(LOGIN_KEY, login)
            userFragment.arguments = bundle
            return userFragment
        }
    }
}