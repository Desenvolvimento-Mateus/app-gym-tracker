package com.mateusavila.gymtracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.WorkoutRepository
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.startProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException

/**
 * Receives only the [exerciseId] from the route and looks up the rest in the repository.
 * The progress is changed in the repository, so the home list sees the same values.
 */
class ExerciseDetailViewModel(
    private val exerciseId: String,
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseDetailUiState>(ExerciseDetailUiState.Loading)
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadExercise()
    }

    fun loadExercise() {
        loadJob?.cancel()
        _uiState.value = ExerciseDetailUiState.Loading

        loadJob = viewModelScope.launch {
            val exercise = try {
                repository.getExercise(exerciseId)
            } catch (e: IOException) {
                _uiState.value = ExerciseDetailUiState.Error(R.string.error_reading_workout, canRetry = true)
                return@launch
            } catch (e: SerializationException) {
                _uiState.value = ExerciseDetailUiState.Error(R.string.error_invalid_workout, canRetry = true)
                return@launch
            } catch (e: IllegalArgumentException) {
                _uiState.value = ExerciseDetailUiState.Error(R.string.error_invalid_workout, canRetry = true)
                return@launch
            }

            if (exercise == null) {
                _uiState.value = ExerciseDetailUiState.Error(R.string.exercise_not_found, canRetry = false)
                return@launch
            }

            repository.progress
                .map { progressById -> progressById[exerciseId] ?: exercise.startProgress() }
                .collect { progress -> _uiState.value = ExerciseDetailUiState.Success(exercise.toDetailUi(progress)) }
        }
    }

    fun onSetClick(setNumber: Int) = updateProgress { it.copy(sets = it.sets.toggleSet(setNumber)) }

    fun onCompleteSet() = updateProgress { it.copy(sets = it.sets.completeSet()) }

    fun onUndoSet() = updateProgress { it.copy(sets = it.sets.undoSet()) }

    fun onIncreaseWeight() = updateProgress { it.increaseWeight() }

    fun onDecreaseWeight() = updateProgress { it.decreaseWeight() }

    private fun updateProgress(change: (ExerciseProgress) -> ExerciseProgress) {
        repository.updateProgress(exerciseId, change)
    }
}
