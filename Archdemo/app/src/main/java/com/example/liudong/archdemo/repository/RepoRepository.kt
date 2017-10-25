package com.example.liudong.archdemo.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.example.liudong.archdemo.AppExecutors
import com.example.liudong.archdemo.api.ApiResponse
import com.example.liudong.archdemo.api.GithubService
import com.example.liudong.archdemo.api.RepoSearchResponse
import com.example.liudong.archdemo.db.GithubDb
import com.example.liudong.archdemo.db.RepoDao
import com.example.liudong.archdemo.util.AbsentLiveData
import com.example.liudong.archdemo.util.RateLimiter
import com.example.liudong.archdemo.vo.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles Repo instances.
 *
 * unfortunate naming :/ .
 * Repo - value object name
 * Repository - type of this class.
 */
@Singleton
class RepoRepository @Inject constructor(
        private val appExecutors: AppExecutors,
        private val db: GithubDb,
        private val repoDao: RepoDao,
        private val githubService: GithubService) {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>?) {
                item?.let { repoDao.insertRepos(it) }
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return repoDao.loadRepositories(owner)
            }

            override fun createCall(): LiveData<ApiResponse<List<Repo>>> {
                return githubService.getRepos(owner)
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(owner)
            }
        }.asLiveData()
    }

    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>(appExecutors) {
            override fun saveCallResult(item: Repo?) {
                item?.let { repoDao.insert(it) }
            }

            override fun shouldFetch(data: Repo?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<Repo> {
                return repoDao.load(owner, name)
            }

            override fun createCall(): LiveData<ApiResponse<Repo>> {
                return githubService.getRepo(owner, name)
            }
        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            override fun saveCallResult(item: List<Contributor>?) {
                if (item == null) return

                for (contributor in item) {
                    contributor.repoName = name
                    contributor.repoOwner = owner
                }
                db.beginTransaction()
                try {
                    repoDao.createRepoIfNotExists(Repo(Repo.UNKNOWN_ID,
                            name, owner + "/" + name, "",
                            Owner(owner, ""), 0))
                    repoDao.insertContributors(item)
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Contributor>> {
                return repoDao.loadContributors(owner, name)
            }

            override fun createCall(): LiveData<ApiResponse<List<Contributor>>> {
                return githubService.getContributors(owner, name)
            }
        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
                query, githubService, db)
        appExecutors.networkIO.execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(query: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {

            override fun saveCallResult(item: RepoSearchResponse?) {
                if (item == null) return
                val repoIds = item.repoIds
                val repoSearchResult = RepoSearchResult(
                        query, repoIds, item.total, item.nextPage)
                db.beginTransaction()
                try {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query), { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(searchData.repoIds)
                    }
                })
            }

            override fun createCall(): LiveData<ApiResponse<RepoSearchResponse>> {
                return githubService.searchRepos(query)
            }

            override fun processResponse(response: ApiResponse<RepoSearchResponse>): RepoSearchResponse? {
                val body = response.body
                if (body != null) {
                    body.nextPage = response.nextPage
                }
                return body
            }
        }.asLiveData()
    }
}
