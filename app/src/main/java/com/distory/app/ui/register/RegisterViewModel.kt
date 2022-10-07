package com.distory.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distory.app.data.story.remote.dto.RequestRegister
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.usecase.RegisterNewUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCase: RegisterNewUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RegisterActivityState>(RegisterActivityState.Init)
    val state: StateFlow<RegisterActivityState> get() = _state

    private val _result = MutableStateFlow(StatusAndMessage())
    val result: StateFlow<StatusAndMessage> get() = _result

    private fun setLoading() {
        _state.value = RegisterActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = RegisterActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = RegisterActivityState.ShowToast(message)
    }

    fun registerNewUser(name: String, email: String, password: String) {
        val request = RequestRegister(
            name = name,
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
                            _result.value = result.data
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

sealed class RegisterActivityState {
    object Init : RegisterActivityState()
    data class ShowToast(val message: String) : RegisterActivityState()
    data class IsLoading(val isLoading: Boolean) : RegisterActivityState()
}