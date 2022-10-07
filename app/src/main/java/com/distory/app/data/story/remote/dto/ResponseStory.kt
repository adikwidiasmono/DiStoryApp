package com.distory.app.data.story.remote.dto

import com.google.gson.annotations.SerializedName

data class ResponseStory(
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var message: String? = null,
    @SerializedName("listStory") var listStory: ArrayList<ResponseStoryData> = arrayListOf()
)

data class ResponseStoryData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("photoUrl") var photoUrl: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("lat") var latitude: Double? = null,
    @SerializedName("lon") var longitude: Double? = null
)