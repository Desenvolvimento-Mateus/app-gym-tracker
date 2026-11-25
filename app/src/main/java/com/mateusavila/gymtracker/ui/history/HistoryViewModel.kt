package com.mateusavila.gymtracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException

class HistoryViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        loadJob?.cancel()
        _uiState.value = HistoryUiState.Loading

        loadJob = viewModelScope.launch {
            _uiState.value = try {
                repository.getHistory().toHistoryUiState()
            } catch (e: IOException) {
                HistoryUiState.Error(R.string.error_reading_history)
            } catch (e: SerializationException) {
                // The history file exists but its content is not a valid JSON list.
                HistoryUiState.Error(R.string.error_invalid_history)
            }
        }
    }
}
