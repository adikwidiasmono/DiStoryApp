package com.distory.app.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distory.app.domain.common.BaseResult
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.domain.story.usecase.FetchStoriesWithLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryMapsViewModel @Inject constructor(
    private val fetchStoriesWithLocationUseCase: FetchStoriesWithLocationUseCase
) : ViewModel() {
    private val _state = MutableLiveData<StoryMapsActivityState>(StoryMapsActivityState.Init)
    val state: LiveData<StoryMapsActivityState> get() = _state

    private val _stories: MutableLiveData<List<StoryEntity>> = MutableLiveData(listOf())
    val stories: LiveData<List<StoryEntity>> get() = _stories

    private fun setLoading() {
        _state.value = StoryMapsActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = StoryMapsActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = StoryMapsActivityState.ShowToast(message)
    }

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            fetchStoriesWithLocationUseCase.invoke()
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
                            _stories.value = result.data as MutableList<StoryEntity>
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

sealed class StoryMapsActivityState {
    object Init : StoryMapsActivityState()
    data class ShowToast(val message: String) : StoryMapsActivityState()
    data class IsLoading(val isLoading: Boolean) : StoryMapsActivityState()
}