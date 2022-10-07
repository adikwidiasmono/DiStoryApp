package com.distory.app.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.distory.app.domain.story.entity.StoryEntity
import com.distory.app.domain.story.usecase.FetchStoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val fetchStoriesUseCase: FetchStoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<StoryActivityState>(StoryActivityState.Init)
    val state: StateFlow<StoryActivityState> get() = _state

    private val _stories = MutableStateFlow(PagingData.empty<StoryEntity>())
    val stories: StateFlow<PagingData<StoryEntity>> get() = _stories

    private fun setLoading() {
        _state.value = StoryActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = StoryActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        _state.value = StoryActivityState.ShowToast(message)
    }

    fun fetchStories() {
        viewModelScope.launch {
            fetchStoriesUseCase.invoke().cachedIn(viewModelScope)
                .onStart {
                    setLoading()
                }
                .catch { e ->
                    hideLoading()
                    showToast(e.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    _stories.value = result
                }
        }
    }
}

sealed class StoryActivityState {
    object Init : StoryActivityState()
    data class ShowToast(val message: String) : StoryActivityState()
    data class IsLoading(val isLoading: Boolean) : StoryActivityState()
}