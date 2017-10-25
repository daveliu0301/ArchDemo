package com.example.liudong.archdemo.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.example.liudong.archdemo.AppExecutors
import com.example.liudong.archdemo.api.ApiResponse
import com.example.liudong.archdemo.vo.Resource
import com.example.liudong.archdemo.vo.Resource.Companion.loading

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread internal constructor(
        private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = loading(null)
        val dbSource = loadFromDb()
        result.addSource(dbSource, {
            result.removeSource(dbSource)
            if (shouldFetch(it)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, { newData -> result.setValue(Resource.success(newData)) })
            }
        })
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, { newData -> result.setValue(loading(newData)) })
        result.addSource(apiResponse, { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response!!.isSuccessful) {
                appExecutors.diskIO.execute({
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread.execute({
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb(),
                                { newData -> result.setValue(Resource.success(newData)) })
                    }
                    )
                })
            } else {
                onFetchFailed()
                result.addSource(dbSource,
                        { newData -> result.setValue(Resource.error(response.errorMessage, newData)) })
            }
        })
    }

    open fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    open fun processResponse(response: ApiResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType?)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
