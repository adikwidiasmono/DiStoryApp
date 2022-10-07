package com.distory.app.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.distory.app.DataDummy
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.usecase.FetchStoriesWithLocationUseCase
import com.distory.app.utils.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryMapsViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: FetchStoriesWithLocationUseCase

    private lateinit var viewModel: StoryMapsViewModel
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesEntity()

    @Before
    fun setUp() {
        viewModel = StoryMapsViewModel(useCase)
    }

    @Test
    fun `when Get Stories with location Return SUCCESS`(): Unit = runTest {
        val expectedStories = flowOf(BaseResult.Success(dummyStoriesResponse))

        `when`(useCase.invoke()).thenReturn(expectedStories)

        viewModel.fetchStoriesWithLocation()
        val actualStories = viewModel.stories.value

        verify(useCase).invoke()
        Assert.assertSame(dummyStoriesResponse, actualStories)
    }

    @Test
    fun `when Get Stories with location Return FAILED`(): Unit = runTest {
        val expectedStories = flowOf(BaseResult.Error(Failure(-99, "No Data Found")))

        `when`(useCase.invoke()).thenReturn(expectedStories)

        viewModel.fetchStoriesWithLocation()
        val actualStories = viewModel.stories.value

        verify(useCase).invoke()
        Assert.assertNotNull(actualStories)
        actualStories?.let {
            Assert.assertTrue(it.isEmpty())
        }
    }
}