package com.mateusavila.gymtracker.ui.home

import androidx.annotation.StringRes
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.startProgress

/** Everything the home screen can show. Exactly one of these states at a time. */
sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Error(@param:StringRes val messageRes: Int) : HomeUiState

    /** The workout file was read, but it has no exercises. */
    data object Empty : HomeUiState

    data class Success(
        val workoutName: String,
        val summary: WorkoutSummary,
        val exercises: List<ExerciseListItem>,
        val canFinishWorkout: Boolean,
        val isFinishing: Boolean
    ) : HomeUiState
}

/** One-time events: they happen once and are not part of the screen state. */
sealed interface HomeEvent {
    data object WorkoutFinished : HomeEvent
    data object FinishFailed : HomeEvent
}

/** Maps the domain (workout + progress) to the state shown by the screen. */
fun Workout.toHomeSuccessState(
    progressById: Map<String, ExerciseProgress>,
    isFinishing: Boolean
): HomeUiState.Success {
    val summary = exercises.toWorkoutSummary(progressById)
    return HomeUiState.Success(
        workoutName = name,
        summary = summary,
        exercises = exercises.mapIndexed { index, exercise ->
            exercise.toListItem(
                order = index + 1,
                progress = progressById[exercise.id] ?: exercise.startProgress()
            )
        },
        // Only a workout with at least one set done can be saved in the history.
        canFinishWorkout = summary.completedSets > 0 && !isFinishing,
        isFinishing = isFinishing
    )
}
