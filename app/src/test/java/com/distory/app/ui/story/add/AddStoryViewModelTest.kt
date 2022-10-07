package com.distory.app.ui.story.add

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.distory.app.DataDummy
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.usecase.CreateNewStoryUseCase
import com.distory.app.utils.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: CreateNewStoryUseCase

    private lateinit var viewModel: AddStoryViewModel
    private val dummySuccessResponse = DataDummy.generateDummySuccessStatusAndMessage()

    @Mock
    private lateinit var dummyUri : Uri
    private val dummyDesc = "Here is my story"
    private val dummyLat = java.util.Random().nextDouble()
    private val dummyLon = java.util.Random().nextDouble()

    @Before
    fun setUp() {
        viewModel = AddStoryViewModel(useCase)
    }

    @Test
    fun `when Create New Story Return SUCCESS`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Success(dummySuccessResponse))

        `when`(
            useCase.invoke(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedResult)

        viewModel.addNewStory(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )
        val actualResult = viewModel.result.value

        Mockito.verify(useCase).invoke(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )
        assertSame(dummySuccessResponse, actualResult)
    }

    @Test
    fun `when Create New Story Return FAILED`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Error(Failure(-99, "Failed")))

        `when`(
            useCase.invoke(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedResult)

        viewModel.addNewStory(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )
        val actualResult = viewModel.result.value

        Mockito.verify(useCase).invoke(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )
        assertTrue(actualResult.isError)
    }
}