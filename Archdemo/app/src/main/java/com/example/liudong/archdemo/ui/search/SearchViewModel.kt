package com.example.liudong.archdemo.ui.search

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.support.annotation.VisibleForTesting
import com.example.liudong.archdemo.repository.RepoRepository
import com.example.liudong.archdemo.util.AbsentLiveData
import com.example.liudong.archdemo.vo.Repo
import com.example.liudong.archdemo.vo.Resource
import com.example.liudong.archdemo.vo.Status
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        repoRepository: RepoRepository
) : ViewModel() {

    private val query = MutableLiveData<String>()

    val results: LiveData<Resource<List<Repo>>> = Transformations.switchMap(query) { search ->
        Timber.d("Timber search: " + search)
        if (search == null || search.trim { it <= ' ' }.isEmpty()) {
            AbsentLiveData.create()
        } else {
            repoRepository.search(search)
        }
    }

    private val nextPageHandler: NextPageHandler = NextPageHandler(repoRepository)

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim { it <= ' ' }
        if (input == query.value) {
            return
        }
        nextPageHandler.reset()
        Timber.d("Timber query.value: " + input)
        query.value = input
    }

    fun loadNextPage() {
        val value = query.value
        if (value != null && !value.trim { it <= ' ' }.isEmpty()) {
            nextPageHandler.queryNextPage(value)
        }
    }

    fun refresh() {
        if (query.value != null) {
            query.value = query.value
        }
    }

    class LoadMoreState(val isRunning: Boolean, private val errorMessage: String) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler constructor(private val repository: RepoRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        @VisibleForTesting
        private var hasMore: Boolean = true

        init {
            reset()
        }

        fun reset() {
            unregister()
            hasMore = true
            loadMoreState.value = LoadMoreState(false, "")
        }

        private fun unregister() {
            if (nextPageLiveData != null) {
                nextPageLiveData!!.removeObserver(this)
                nextPageLiveData = null
                if (hasMore) {
                    query = null
                }
            }
        }

        fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(true, "")

            nextPageLiveData!!.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        hasMore = result.data ?: false
                        unregister()
                        loadMoreState.value = LoadMoreState(false, "")
                    }
                    Status.ERROR -> {
                        hasMore = true
                        unregister()
                        loadMoreState.value = LoadMoreState(false, result.message ?: "")
                    }
                    else -> {
                    }
                }
            }
        }
    }
}