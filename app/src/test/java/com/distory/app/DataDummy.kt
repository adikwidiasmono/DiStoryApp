package com.distory.app

import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.entity.StoryEntity
import java.util.Random

object DataDummy {
    fun generateDummyStoriesEntity(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 0..10) {
            val story = StoryEntity(
                id = "ID ${random()}",
                name = "Name ${random()}",
                description = "Description ${random()}",
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                createdAt = "2022-02-22T22:22:22Z",
                latitude = Random().nextDouble(),
                longitude = Random().nextDouble()
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummySuccessStatusAndMessage() = StatusAndMessage(
        isError = false,
        message = "Success"
    )

    fun generateDummyLoginUser() = LoginUser(
        userId = "User ID ${random()}",
        name = "Name ${random()}",
        token = "Token-${random()}-Secure"
    )

    private fun random() = (0..99999).random().toString()
}