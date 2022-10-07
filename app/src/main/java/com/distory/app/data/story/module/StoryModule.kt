package com.distory.app.data.story.module

import com.distory.app.data.AppDatabase
import com.distory.app.data.story.StoryRepositoryImpl
import com.distory.app.data.story.local.RemoteKeysDao
import com.distory.app.data.story.local.StoryDao
import com.distory.app.data.story.remote.StoryRemoteSource
import com.distory.app.data.story.remote.api.StoryApi
import com.distory.app.domain.story.repo.StoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoryModule {

    @Provides
    @Singleton
    fun provideStoryRemoteApi(retrofit: Retrofit): StoryApi {
        return retrofit.create(StoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStoryRemoteSource(storyApi: StoryApi): StoryRemoteSource {
        return StoryRemoteSource(storyApi)
    }

    @Provides
    @Singleton
    fun provideStoryDao(appDatabase: AppDatabase): StoryDao {
        return appDatabase.storyDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(appDatabase: AppDatabase): RemoteKeysDao {
        return appDatabase.remoteKeysDao()
    }

    @Provides
    @Singleton
    fun provideStoryRepository(
        storyRemoteSource: StoryRemoteSource,
        appDatabase: AppDatabase
    ): StoryRepository {
        return StoryRepositoryImpl(storyRemoteSource, appDatabase)
    }
}