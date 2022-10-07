package com.distory.app.data.story.remote

import android.net.Uri
import com.distory.app.data.common.exception.NoInternetConnectionException
import com.distory.app.data.story.remote.api.StoryApi
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.entity.StoryEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StoryRemoteSource constructor(private val storyApi: StoryApi) {

    suspend fun addStory(
        uri: Uri, desc: String, lat: Double, lon: Double
    ): BaseResult<StatusAndMessage, Failure> {
        try {
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault()
            ).format(Date())

            val reqDesc = desc.toRequestBody("text/plain".toMediaTypeOrNull())
            val reqLat = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val reqLon = lon.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val reqFile = File(uri.path!!).asRequestBody("image/*".toMediaTypeOrNull())
            val reqBody = MultipartBody.Part.createFormData("photo", "$timeStamp.png", reqFile)

            val response = storyApi.addStory(
                desc = reqDesc,
                lat = reqLat,
                lon = reqLon,
                file = reqBody
            )
            return if (response.isSuccessful) {
                val result = response.body()
                if (result != null && !result.error) {
                    BaseResult.Success(
                        StatusAndMessage(
                            isError = result.error,
                            message = result.message ?: "-"
                        )
                    )
                } else {
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                }
            } else {
                try {
                    val result = response.body()
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                } catch (e: Exception) {
                    BaseResult.Error(Failure(response.code(), response.message()))
                }
            }
        } catch (e: NoInternetConnectionException) {
            return BaseResult.Error(Failure(0, e.message))
        } catch (e: Exception) {
            return BaseResult.Error(Failure(-1, e.message.toString()))
        }
    }

    suspend fun fetchStories(page: Int? = null, size: Int? = null, location: String? = null)
            : BaseResult<List<StoryEntity>, Failure> {
        try {
            val response = storyApi.stories(page = page, size = size, location = location)
            return if (response.isSuccessful) {
                val result = response.body()
                if (result != null && !result.error) {
                    val stories = mutableListOf<StoryEntity>()
                    result.listStory.forEach { story ->
                        stories.add(
                            StoryEntity(
                                id = story.id ?: "-99",
                                name = story.name ?: "-",
                                description = story.description ?: "-",
                                photoUrl = story.photoUrl ?: "",
                                createdAt = story.createdAt ?: "",
                                latitude = story.latitude ?: 0.0,
                                longitude = story.longitude ?: 0.0
                            )
                        )
                    }
                    BaseResult.Success(stories)
                } else {
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                }
            } else {
                try {
                    val result = response.body()
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                } catch (e: Exception) {
                    BaseResult.Error(Failure(response.code(), response.message()))
                }
            }
        } catch (e: NoInternetConnectionException) {
            return BaseResult.Error(Failure(0, e.message))
        } catch (e: Exception) {
            return BaseResult.Error(Failure(-1, e.message.toString()))
        }
    }

    suspend fun register(request: RequestRegister): BaseResult<StatusAndMessage, Failure> {
        try {
            val response = storyApi.register(request)
            return if (response.isSuccessful) {
                val result = response.body()
                if (result != null && !result.error) {
                    BaseResult.Success(
                        StatusAndMessage(
                            isError = result.error,
                            message = result.message ?: "-"
                        )
                    )
                } else {
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                }
            } else {
                try {
                    val result = response.body()
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                } catch (e: Exception) {
                    BaseResult.Error(Failure(response.code(), response.message()))
                }
            }
        } catch (e: NoInternetConnectionException) {
            return BaseResult.Error(Failure(0, e.message))
        } catch (e: Exception) {
            return BaseResult.Error(Failure(-1, e.message.toString()))
        }
    }

    suspend fun login(request: RequestLogin): BaseResult<LoginUser, Failure> {
        try {
            val response = storyApi.login(request)
            return if (response.isSuccessful) {
                val result = response.body()
                if (result?.loginResult != null && !result.error) {
                    BaseResult.Success(
                        LoginUser(
                            userId = result.loginResult!!.userId ?: "-99",
                            name = result.loginResult!!.name ?: "-",
                            token = result.loginResult!!.token ?: ""
                        )
                    )
                } else {
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                }
            } else {
                try {
                    val result = response.body()
                    BaseResult.Error(
                        Failure
                            (response.code(), result?.message ?: response.message())
                    )
                } catch (e: Exception) {
                    BaseResult.Error(Failure(response.code(), response.message()))
                }
            }
        } catch (e: NoInternetConnectionException) {
            return BaseResult.Error(Failure(0, e.message))
        } catch (e: Exception) {
            return BaseResult.Error(Failure(-1, e.message.toString()))
        }
    }


}