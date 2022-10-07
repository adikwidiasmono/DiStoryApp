package com.distory.app.domain.story.usecase

import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchStoriesWithLocationUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(): Flow<BaseResult<List<StoryEntity>, Failure>> {
        return storyRepository.fetchStoriesWithLocation()
    }
}