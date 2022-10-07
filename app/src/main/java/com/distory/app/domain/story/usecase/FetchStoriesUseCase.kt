package com.distory.app.domain.story.usecase

import androidx.paging.PagingData
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchStoriesUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(): Flow<PagingData<StoryEntity>> {
        return storyRepository.fetchStories()
    }
}