package com.example.liudong.archdemo.ui.repo

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.liudong.archdemo.databinding.RepoFragmentBinding
import com.example.liudong.archdemo.di.Injectable
import com.example.liudong.archdemo.ui.common.NavigationController
import com.example.liudong.archdemo.ui.common.RetryCallback
import com.example.liudong.archdemo.util.AutoClearedValue
import com.example.liudong.archdemo.vo.Contributor
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import timber.log.Timber
import javax.inject.Inject

/**
 * The UI Controller for displaying a Github Repo's information with its contributors.
 */
class RepoFragment : Fragment(), Injectable {

    private val lifecycleRegistry = LifecycleRegistry(this)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var repoViewModel: RepoViewModel
    private lateinit var binding: AutoClearedValue<RepoFragmentBinding>
    private lateinit var adapter: AutoClearedValue<ContributorAdapter>

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        repoViewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoViewModel::class.java)
        val args = arguments
        val owner = args?.getString(REPO_OWNER_KEY) ?: ""
        val name = args?.getString(REPO_NAME_KEY) ?: ""

        repoViewModel.setId(owner, name)
        val repo = repoViewModel.repo
        repo.observe(this, Observer<Resource<Repo>> {

            Timber.d("Timber status: " + it?.status)
            Timber.d("Timber message: " + it?.message)
            Timber.d("Timber repo: " + it?.data)

            binding.value!!.repo = it?.data
            binding.value!!.repoResource = it
            binding.value!!.executePendingBindings()
        })

        adapter = AutoClearedValue(this, ContributorAdapter({
            navigationController.navigateToUser(it.login ?: "")
        }))
        binding.value!!.contributorList.adapter = adapter.value
        initContributorList(repoViewModel)
    }

    private fun initContributorList(viewModel: RepoViewModel) {
        viewModel.contributors.observe(this, Observer<Resource<List<Contributor>>> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            adapter.value!!.replace(it?.data ?: emptyList())
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = RepoFragmentBinding.inflate(inflater)
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                repoViewModel.retry()
            }
        }
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    companion object {
        val REPO_OWNER_KEY = "repo_owner"
        val REPO_NAME_KEY = "repo_name"
        fun create(owner: String, name: String): RepoFragment {
            val repoFragment = RepoFragment()
            val args = Bundle()
            args.putString(REPO_OWNER_KEY, owner)
            args.putString(REPO_NAME_KEY, name)
            repoFragment.arguments = args
            return repoFragment
        }
    }
}