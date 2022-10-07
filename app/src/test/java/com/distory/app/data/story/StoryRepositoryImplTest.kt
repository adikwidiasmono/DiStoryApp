package com.distory.app.data.story

import android.net.Uri
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.recyclerview.widget.ListUpdateCallback
import com.distory.app.DataDummy
import com.distory.app.data.AppDatabase
import com.distory.app.data.story.remote.StoryRemoteSource
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.ui.story.StoryAdapter
import com.distory.app.utils.CoroutinesTestRule
import com.distory.app.utils.PagedTestDataSource
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
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryImplTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var storyRemoteSource: StoryRemoteSource

    @Mock
    private lateinit var appDatabase: AppDatabase

    @Mock
    private lateinit var repoMock: StoryRepositoryImpl

    private lateinit var repo: StoryRepositoryImpl

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesEntity()
    private val dummySuccessResponse = DataDummy.generateDummySuccessStatusAndMessage()
    private val dummyLoginUserResponse = DataDummy.generateDummyLoginUser()

    private val dummyName = "Adik"
    private val dummyEmail = "adik@mail.com"
    private val dummyPassword = "password"

    @Mock
    private lateinit var dummyUri: Uri
    private val dummyDesc = "Here is my story"
    private val dummyLat = java.util.Random().nextDouble()
    private val dummyLon = java.util.Random().nextDouble()

    @Before
    fun setup() {
        repo = StoryRepositoryImpl(storyRemoteSource, appDatabase)
    }

    @Test
    fun `when Register New User Return SUCCESS`() = runTest {
        val expectedResult = BaseResult.Success(dummySuccessResponse)
        val requestData = RequestRegister(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )

        `when`(storyRemoteSource.register(requestData)).thenReturn(expectedResult)

        repo.registerNewUser(requestData).collect { result ->
            verify(storyRemoteSource).register(requestData)
            Assert.assertTrue(result is BaseResult.Success)
        }
    }

    @Test
    fun `when Register New User Return FAILED`() = runTest {
        val expectedResult = BaseResult.Error(Failure(-99, "Failed"))
        val requestData = RequestRegister(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )

        `when`(storyRemoteSource.register(requestData)).thenReturn(expectedResult)

        repo.registerNewUser(requestData).collect { result ->
            verify(storyRemoteSource).register(requestData)
            Assert.assertTrue(result is BaseResult.Error)
        }
    }

    @Test
    fun `when User Sign In Return SUCCESS`() = runTest {
        val expectedResult = BaseResult.Success(dummyLoginUserResponse)
        val requestData = RequestLogin(
            email = dummyEmail,
            password = dummyPassword
        )

        `when`(storyRemoteSource.login(requestData)).thenReturn(expectedResult)

        repo.validateSignInUser(requestData).collect { result ->
            verify(storyRemoteSource).login(requestData)
            Assert.assertTrue(result is BaseResult.Success)
        }
    }

    @Test
    fun `when User Sign In Return FAILED`() = runTest {
        val expectedResult = BaseResult.Error(Failure(-99, "Failed"))
        val requestData = RequestLogin(
            email = dummyEmail,
            password = dummyPassword
        )

        `when`(storyRemoteSource.login(requestData)).thenReturn(expectedResult)

        repo.validateSignInUser(requestData).collect { result ->
            verify(storyRemoteSource).login(requestData)
            Assert.assertTrue(result is BaseResult.Error)
        }
    }

    @Test
    fun `when Get Stories Return SUCCESS`() = runTest {
//        OLD - Submitted but reject

//        val dummyStories = DataDummy.generateDummyStoriesEntity()
//        val data = PagedTestDataSource.snapshot(dummyStories)
//
//        val expectedResult = flowOf(data)
//
//        `when`(repoMock.fetchStories()).thenReturn(expectedResult)
//
//        repoMock.fetchStories().collect { result ->
//            val differ = AsyncPagingDataDiffer(
//                diffCallback = StoryAdapter.DIFF_CALLBACK,
//                updateCallback = noopListUpdateCallback,
//                mainDispatcher = coroutinesTestRule.testDispatcher,
//                workerDispatcher = coroutinesTestRule.testDispatcher
//            )
//            differ.submitData(result)
//            Assert.assertNotNull(differ.snapshot())
//            Assert.assertEquals(
//                dummyStoriesResponse.size,
//                differ.snapshot().size
//            )
//        }

//        CURRENT

        val expectedStories = BaseResult.Success(dummyStoriesResponse)
        val data = PagedTestDataSource.snapshot(dummyStoriesResponse)

//        val expectedResult = flowOf(data)

        val expectedResult = PagingSource.LoadResult.Page(
            data = dummyStoriesResponse,
            prevKey = null,
            nextKey = 1
        )

        `when`(storyRemoteSource.fetchStories()).thenReturn(expectedStories)
        `when`(appDatabase.storyDao().findAll()).thenReturn(expectedResult)

        repo.fetchStories().collect { result ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutinesTestRule.testDispatcher,
                workerDispatcher = coroutinesTestRule.testDispatcher
            )
            differ.submitData(result)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(
                dummyStoriesResponse.size,
                differ.snapshot().size
            )
        }
    }

    @Test
    fun `when Get Stories with location Return SUCCESS`() = runTest {
        val expectedStories = BaseResult.Success(dummyStoriesResponse)
        val storyWithLocation = "1"
        val page = 1
        val pageSize = 20

        `when`(
            storyRemoteSource.fetchStories(page, pageSize, storyWithLocation)
        ).thenReturn(expectedStories)

        repo.fetchStoriesWithLocation().collect { result ->
            verify(storyRemoteSource)
                .fetchStories(page, pageSize, storyWithLocation)
            Assert.assertTrue(result is BaseResult.Success)
            if (result is BaseResult.Success)
                Assert.assertSame(dummyStoriesResponse, result.data)
        }
    }

    @Test
    fun `when Get Stories with location Return FAILED`() = runTest {
        val expectedStories = BaseResult.Error(Failure(-99, "No Data Found"))
        val storyWithLocation = "1"
        val page = 1
        val pageSize = 20

        `when`(
            storyRemoteSource.fetchStories(page, pageSize, storyWithLocation)
        ).thenReturn(expectedStories)

        repo.fetchStoriesWithLocation().collect { result ->
            verify(storyRemoteSource)
                .fetchStories(page, pageSize, storyWithLocation)
            Assert.assertTrue(result is BaseResult.Error)
        }
    }

    @Test
    fun `when Create New Story Return SUCCESS`() = runTest {
        val expectedResult = BaseResult.Success(dummySuccessResponse)

        `when`(
            storyRemoteSource.addStory(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedResult)

        repo.addNewStory(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        ).collect { result ->
            verify(storyRemoteSource).addStory(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
            Assert.assertTrue(result is BaseResult.Success)
        }
    }

    @Test
    fun `when Create New Story Return FAILED`() = runTest {
        val expectedResult = BaseResult.Error(Failure(-99, "Failed"))

        `when`(
            storyRemoteSource.addStory(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedResult)

        repo.addNewStory(
            uri = dummyUri,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        ).collect { result ->
            verify(storyRemoteSource).addStory(
                uri = dummyUri,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
            Assert.assertTrue(result is BaseResult.Error)
        }
    }
}

private val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}