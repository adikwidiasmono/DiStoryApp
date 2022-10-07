package com.distory.app.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import com.distory.app.DataDummy
import com.distory.app.domain.story.usecase.FetchStoriesUseCase
import com.distory.app.utils.CoroutinesTestRule
import com.distory.app.utils.PagedTestDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
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
class StoryViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: FetchStoriesUseCase

    private lateinit var viewModel: StoryViewModel
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesEntity()

    @Before
    fun setUp() {
        viewModel = StoryViewModel(useCase)
    }

    @Test
    fun `when Get Stories Return SUCCESS`(): Unit = runTest {
        val data = PagedTestDataSource.snapshot(dummyStoriesResponse)
        val expectedStories = MutableStateFlow(data)

        `when`(useCase.invoke()).thenReturn(expectedStories)

        viewModel.fetchStories()
        val actualStories = viewModel.stories.value

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        verify(useCase).invoke()
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStoriesResponse.size, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}