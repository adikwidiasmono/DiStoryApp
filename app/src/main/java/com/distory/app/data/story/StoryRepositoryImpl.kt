package com.distory.app.data.story

import android.net.Uri
import androidx.paging.*
import com.distory.app.data.AppDatabase
import com.distory.app.data.story.remote.StoryRemoteSource
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoryRepositoryImpl constructor(
    private val storyRemoteSource: StoryRemoteSource,
    private val appDatabase: AppDatabase
) : StoryRepository {
    override suspend fun registerNewUser(request: RequestRegister): Flow<BaseResult<StatusAndMessage, Failure>> {
        return flow {
            emit(storyRemoteSource.register(request))
        }
    }

    override suspend fun validateSignInUser(request: RequestLogin): Flow<BaseResult<LoginUser, Failure>> {
        return flow {
            emit(storyRemoteSource.login(request))
        }
    }

    override suspend fun fetchStories(): Flow<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyRemoteSource, appDatabase),
            pagingSourceFactory = {
//                storyRemoteSource.fetchStories()
                appDatabase.storyDao().findAll()
            }
        ).flow
    }

    override suspend fun fetchStoriesWithLocation(): Flow<BaseResult<List<StoryEntity>, Failure>> {
        return flow {
            val storyWithLocation = "1"
            emit(storyRemoteSource.fetchStories(page = 1, size = 20, location = storyWithLocation))
        }
    }

    override suspend fun addNewStory(
        uri: Uri, desc: String, lat: Double, lon: Double
    ): Flow<BaseResult<StatusAndMessage, Failure>> {
        return flow {
            emit(storyRemoteSource.addStory(uri, desc, lat, lon))
        }
    }
}