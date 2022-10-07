package com.distory.app.ui.story.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.story.entity.StatusAndMessage
import com.distory.app.domain.story.usecase.CreateNewStoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val useCase: CreateNewStoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AddStoryActivityState>(AddStoryActivityState.Init)
    val state: StateFlow<AddStoryActivityState> get() = _state

    private val _result = MutableStateFlow(StatusAndMessage())
    val result: StateFlow<StatusAndMessage> get() = _result

    private fun setLoading() {
        _state.value = AddStoryActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = AddStoryActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = AddStoryActivityState.ShowToast(message)
    }

    fun addNewStory(uri: Uri, desc: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            useCase.invoke(uri, desc, lat, lon)
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

sealed class AddStoryActivityState {
    object Init : AddStoryActivityState()
    data class ShowToast(val message: String) : AddStoryActivityState()
    data class IsLoading(val isLoading: Boolean) : AddStoryActivityState()
}