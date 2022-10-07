package com.distory.app.domain.story.usecase

import android.net.Uri
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateNewStoryUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(uri: Uri, desc: String, lat: Double, lon: Double)
            : Flow<BaseResult<StatusAndMessage, Failure>> {
        return storyRepository.addNewStory(uri, desc, lat, lon)
    }
}