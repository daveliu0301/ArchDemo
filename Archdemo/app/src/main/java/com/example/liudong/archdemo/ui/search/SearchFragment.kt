package com.example.liudong.archdemo.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.liudong.archdemo.databinding.SearchFragmentBinding
import com.example.liudong.archdemo.di.Injectable
import com.example.liudong.archdemo.ui.common.NavigationController
import com.example.liudong.archdemo.ui.common.RepoListAdapter
import com.example.liudong.archdemo.ui.common.RetryCallback
import com.example.liudong.archdemo.util.AutoClearedValue
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var binding: AutoClearedValue<SearchFragmentBinding>

    private lateinit var adapter: AutoClearedValue<RepoListAdapter>

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = SearchFragmentBinding.inflate(inflater)
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
        initRecyclerView()
        val rvAdapter = RepoListAdapter(true, {
            navigationController.navigateToRepo(it.owner?.login ?: "", it.name ?: "")
        })
        binding.value?.repoList?.adapter = rvAdapter
        adapter = AutoClearedValue(this, rvAdapter)

        initSearchInputListener()

        binding.value!!.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }
    }

    private fun initSearchInputListener() {
        binding.value!!.input.setOnEditorActionListener { v, actionId, _ ->
            val result = actionId == EditorInfo.IME_ACTION_SEARCH
            if (result) {
                doSearch(v)
            }
            result
        }
        binding.value!!.input.setOnKeyListener({ v, keyCode, event ->
            val result = event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
            if (result) {
                doSearch(v)
            }
            result
        })
    }

    private fun doSearch(v: View) {
        val query = binding.value?.input?.text.toString()
        // Dismiss keyboard
        dismissKeyboard(v.windowToken)
        binding.value?.query = query
        searchViewModel.setQuery(query)
    }

    private fun initRecyclerView() {

        binding.value!!.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.value!!.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        searchViewModel.results.observe(this, Observer<Resource<List<Repo>>> { result ->
            binding.value?.searchResource = result
            binding.value?.resultCount = result?.data?.size ?: 0
            adapter.value!!.replace(result?.data)
            binding.value?.executePendingBindings()
        })

        searchViewModel.loadMoreStatus.observe(this, Observer<SearchViewModel.LoadMoreState> { loadingMore ->
            binding.value!!.loadingMore = loadingMore?.isRunning ?: false
            loadingMore?.let {
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.value!!.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
            binding.value?.executePendingBindings()
        })
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        if (activity != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}
