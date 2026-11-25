package com.mateusavila.gymtracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException

class HomeViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Navigating or showing a snackbar must happen only once, so they are sent
    // through a Channel instead of being kept in the state.
    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    private val isFinishing = MutableStateFlow(false)
    private var loadJob: Job? = null

    init {
        loadWorkout()
    }

    /** Also called by the "Tentar novamente" button. */
    fun loadWorkout() {
        loadJob?.cancel()
        _uiState.value = HomeUiState.Loading

        loadJob = viewModelScope.launch {
            val workout = try {
                repository.getWorkout()
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error(R.string.error_reading_workout)
                return@launch
            } catch (e: SerializationException) {
                _uiState.value = HomeUiState.Error(R.string.error_invalid_workout)
                return@launch
            } catch (e: IllegalArgumentException) {
                // The JSON is valid but has invalid values (e.g. totalSets = 0).
                _uiState.value = HomeUiState.Error(R.string.error_invalid_workout)
                return@launch
            }

            if (workout.exercises.isEmpty()) {
                _uiState.value = HomeUiState.Empty
                return@launch
            }

            // Every change of the progress (also the ones made in the detail screen) updates the list.
            combine(repository.progress, isFinishing) { progress, finishing ->
                workout.toHomeSuccessState(progress, finishing)
            }.collect { _uiState.value = it }
        }
    }

    fun finishWorkout() {
        if (isFinishing.value) return

        viewModelScope.launch {
            isFinishing.value = true
            try {
                repository.finishWorkout()
                _events.send(HomeEvent.WorkoutFinished)
            } catch (e: IOException) {
                _events.send(HomeEvent.FinishFailed)
            } catch (e: IllegalArgumentException) {
                // Also covers SerializationException: the saved history file is not valid.
                _events.send(HomeEvent.FinishFailed)
            } finally {
                isFinishing.value = false
            }
        }
    }
}
