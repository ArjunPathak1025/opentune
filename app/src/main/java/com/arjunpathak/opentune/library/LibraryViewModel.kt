package com.arjunpathak.opentune.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocalMusicRepository(application.contentResolver)
    private val _tracks = MutableStateFlow<List<LocalTrack>>(emptyList())
    val tracks: StateFlow<List<LocalTrack>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOrder = MutableStateFlow(TrackSortOrder.TITLE)
    val sortOrder: StateFlow<TrackSortOrder> = _sortOrder.asStateFlow()

    fun refresh() {
        refresh(_sortOrder.value)
    }

    fun setSortOrder(order: TrackSortOrder) {
        if (_sortOrder.value == order) return
        _sortOrder.value = order
        refresh(order)
    }

    private fun refresh(order: TrackSortOrder) {
        viewModelScope.launch {
            _isLoading.value = true
            _tracks.value = repository.getTracks(order)
            _isLoading.value = false
        }
    }
}
