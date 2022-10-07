package com.distory.app.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distory.app.data.story.remote.dto.RequestLogin
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.story.entity.LoginUser
import com.distory.app.domain.story.usecase.ValidateSignInUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val useCase: ValidateSignInUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<SignInActivityState>(SignInActivityState.Init)
    val state: StateFlow<SignInActivityState> get() = _state

    private val _loginUser = MutableStateFlow(LoginUser())
    val loginUser: StateFlow<LoginUser> get() = _loginUser

    private fun setLoading() {
        _state.value = SignInActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = SignInActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = SignInActivityState.ShowToast(message)
    }

    fun validateSignInUser(email: String, password: String) {
        val request = RequestLogin(
            email = email,
            password = password
        )
        viewModelScope.launch {
            useCase.invoke(request)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    showToast(e.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            _loginUser.value = result.data
                        }
                        is BaseResult.Error -> {
                            // 0 means no internet connection
                            val msg = if (result.err.code != 0)
                                "${result.err.message} [${result.err.code}]"
                            else
                                "No Internet Connection"
                            showToast(msg)
                        }
                    }
                }
        }
    }
}

sealed class SignInActivityState {
    object Init : SignInActivityState()
    data class ShowToast(val message: String) : SignInActivityState()
    data class IsLoading(val isLoading: Boolean) : SignInActivityState()
}