package com.distory.app.data.story.remote.dto

import com.google.gson.annotations.SerializedName

data class ResponseGeneral(
    @SerializedName("error"   ) var error   : Boolean  = true,
    @SerializedName("message" ) var message : String?  = null
)
