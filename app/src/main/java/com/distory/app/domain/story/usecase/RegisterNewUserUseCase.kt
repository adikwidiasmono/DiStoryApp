package com.distory.app.domain.story.usecase

import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterNewUserUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(request: RequestRegister): Flow<BaseResult<StatusAndMessage, Failure>> {
        return storyRepository.registerNewUser(request)
    }
}