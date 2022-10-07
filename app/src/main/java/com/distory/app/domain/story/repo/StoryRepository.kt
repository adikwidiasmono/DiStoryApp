package com.distory.app.domain.story.repo

import android.net.Uri
import androidx.paging.PagingData
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    suspend fun registerNewUser(request: RequestRegister): Flow<BaseResult<StatusAndMessage, Failure>>
    suspend fun validateSignInUser(request: RequestLogin): Flow<BaseResult<LoginUser, Failure>>
    suspend fun fetchStories(): Flow<PagingData<StoryEntity>>
    suspend fun fetchStoriesWithLocation(): Flow<BaseResult<List<StoryEntity>, Failure>>
    suspend fun addNewStory(uri: Uri, desc: String, lat: Double, lon: Double)
            : Flow<BaseResult<StatusAndMessage, Failure>>
}