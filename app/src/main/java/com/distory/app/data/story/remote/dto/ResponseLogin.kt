package com.distory.app.data.story.remote.dto

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var message: String? = null,
    @SerializedName("loginResult") var loginResult: LoginResult? = LoginResult()
)

data class LoginResult(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("token") var token: String? = null
)