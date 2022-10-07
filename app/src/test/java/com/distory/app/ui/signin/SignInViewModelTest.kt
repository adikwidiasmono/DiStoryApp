package com.distory.app.ui.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.distory.app.DataDummy
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.common.Failure
import com.distory.app.domain.story.usecase.ValidateSignInUserUseCase
import com.distory.app.utils.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignInViewModelTest {
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var useCase: ValidateSignInUserUseCase

    private lateinit var viewModel: SignInViewModel
    private val dummySuccessResponse = DataDummy.generateDummyLoginUser()

    private val dummyEmail = "adik@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        viewModel = SignInViewModel(useCase)
    }

    @Test
    fun `when User Sign In Return SUCCESS`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Success(dummySuccessResponse))
        val requestData = RequestLogin(
            email = dummyEmail,
            password = dummyPassword
        )

        Mockito.`when`(useCase.invoke(requestData)).thenReturn(expectedResult)

        viewModel.validateSignInUser(
            email = dummyEmail,
            password = dummyPassword
        )
        val actualResult = viewModel.loginUser.value

        Mockito.verify(useCase).invoke(requestData)
        assertSame(dummySuccessResponse, actualResult)
    }

    @Test
    fun `when User Sign In Return FAILED`(): Unit = runTest {
        val expectedResult = flowOf(BaseResult.Error(Failure(-99, "Failed")))
        val requestData = RequestLogin(
            email = dummyEmail,
            password = dummyPassword
        )

        Mockito.`when`(useCase.invoke(requestData)).thenReturn(expectedResult)

        viewModel.validateSignInUser(
            email = dummyEmail,
            password = dummyPassword
        )
        val actualResult = viewModel.loginUser.value

        Mockito.verify(useCase).invoke(requestData)
        assertSame("-99", actualResult.userId)
    }
}