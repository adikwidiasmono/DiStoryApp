package com.distory.app.domain.story.entity

data class StatusAndMessage(
    var isError: Boolean = true,
    var message: String = "-"
)