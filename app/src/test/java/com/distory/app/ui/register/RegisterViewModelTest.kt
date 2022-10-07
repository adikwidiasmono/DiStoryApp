package com.distory.app.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.distory.app.DataDummy
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.usecase.RegisterNewUserUseCase
import com.distory.app.utils.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: RegisterNewUserUseCase

    private lateinit var viewModel: RegisterViewModel
    private val dummySuccessResponse = DataDummy.generateDummySuccessStatusAndMessage()

    private val dummyName = "Adik"
    private val dummyEmail = "adik@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        viewModel = RegisterViewModel(useCase)
    }

    @Test
    fun `when Register New User Return SUCCESS`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Success(dummySuccessResponse))
        val requestData = RequestRegister(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )

        Mockito.`when`(useCase.invoke(requestData)).thenReturn(expectedResult)

        viewModel.registerNewUser(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )
        val actualResult = viewModel.result.value

        Mockito.verify(useCase).invoke(requestData)
        assertFalse(actualResult.isError)
    }

    @Test
    fun `when Register New User Return FAILED`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Error(Failure(-99, "Failed")))

        val requestData = RequestRegister(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )

        Mockito.`when`(useCase.invoke(requestData)).thenReturn(expectedResult)

        viewModel.registerNewUser(
            name = dummyName,
            email = dummyEmail,
            password = dummyPassword
        )
        val actualResult = viewModel.result.value

        Mockito.verify(useCase).invoke(requestData)
        assertTrue(actualResult.isError)
    }
}