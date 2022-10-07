package com.distory.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.distory.app.data.story.local.RemoteKeysDao
import com.distory.app.data.story.local.StoryDao
import com.distory.app.domain.story.entity.RemoteKeysEntity
import com.distory.app.domain.story.entity.StoryEntity

@Database(
    entities = [
        StoryEntity::class, RemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}