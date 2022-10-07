package com.distory.app.data.story

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.distory.app.data.AppDatabase
import com.distory.app.data.story.remote.StoryRemoteSource
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.story.entity.RemoteKeysEntity
import com.distory.app.domain.story.entity.StoryEntity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyRemoteSource: StoryRemoteSource,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            when (val result = storyRemoteSource.fetchStories(page = page, size = state.config.pageSize)) {
                is BaseResult.Success -> {
                    val responseData = result.data

                    val endOfPaginationReached = responseData.isEmpty()

                    //                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        appDatabase.remoteKeysDao().deleteRemoteKeys()
                        appDatabase.storyDao().deleteAll()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map {
                        RemoteKeysEntity(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    appDatabase.remoteKeysDao().insertAll(keys)
                    appDatabase.storyDao().insertAll(responseData)
                    //                }
                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is BaseResult.Error -> {
                    return MediatorResult.Error(Exception(result.err.message))
                }
                else -> {
                    return MediatorResult.Error(Exception("Unknown"))
                }
            }
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            appDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            appDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                appDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}