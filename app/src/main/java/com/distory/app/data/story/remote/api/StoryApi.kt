package com.distory.app.data.story.remote.api

import com.distory.app.data.story.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryApi {
    @POST("register")
    suspend fun register(
        @Body request: RequestRegister
    ): Response<ResponseGeneral>

    @POST("login")
    suspend fun login(
        @Body request: RequestLogin
    ): Response<ResponseLogin>

    @GET("stories")
    suspend fun stories(
        @Header("Authorization") auth: String = "token",
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: String? = "0" // 0: For all stories without considering location, 1: For get all stories with location
    ): Response<ResponseStory>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") auth: String = "token",
        @Part("description") desc: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ResponseGeneral>
}