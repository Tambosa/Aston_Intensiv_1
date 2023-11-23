package com.example.aston_intensiv_1.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aston_intensiv_1.data.MusicRepository
import com.example.aston_intensiv_1.domain.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: MusicRepository) : ViewModel() {
    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    fun getState() {
        viewModelScope.launch {
            if (_state.value == null) {
                _state.value = MainState(
                    musicList = repo.getMusic(),
                    progress = 0,
                    position = 0,
                    isPlaying = false,
                    isShuffle = false
                )
            } else
                _state.value = _state.value
        }
    }

    fun isPlayingChanged() {
        viewModelScope.launch {
            _state.value?.let {
                _state.postValue(it.copy(isPlaying = !it.isPlaying))
            }
        }
    }

    fun getNextSong() {
        _state.value?.let {
            if (it.position + 1 >= it.musicList.size) {
                _state.postValue(it.copy(position = 0))
            } else {
                _state.postValue(it.copy(position = it.position + 1))
            }
        }
    }

    fun getPreviousSong() {
        _state.value?.let {
            if (it.position - 1 < 0) {
                _state.postValue(it.copy(position = it.musicList.size - 1))
            } else {
                _state.postValue(it.copy(position = it.position - 1))
            }
        }
    }
}