package com.distory.app.domain.story.usecase

import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.repo.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ValidateSignInUserUseCase @Inject constructor(private val storyRepository: StoryRepository) {
    suspend fun invoke(request: RequestLogin): Flow<BaseResult<LoginUser, Failure>> {
        return storyRepository.validateSignInUser(request)
    }
}