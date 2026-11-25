package com.mateusavila.gymtracker.ui.detail

import androidx.annotation.StringRes
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress

sealed interface ExerciseDetailUiState {

    data object Loading : ExerciseDetailUiState

    /** [canRetry] is false when trying again cannot help, e.g. an id that does not exist. */
    data class Error(
        @param:StringRes val messageRes: Int,
        val canRetry: Boolean
    ) : ExerciseDetailUiState

    data class Success(val exercise: ExerciseDetailUi) : ExerciseDetailUiState
}

/** UI model of the detail screen: the exercise data plus its current progress. */
data class ExerciseDetailUi(
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val reps: Int,
    val restSeconds: Int,
    val notes: String?,
    val totalSets: Int,
    val completedSets: Int,
    val progressPercentage: Int,
    val isFinished: Boolean,
    val weightInGrams: Int?,
    val canDecreaseWeight: Boolean
)

fun Exercise.toDetailUi(progress: ExerciseProgress) = ExerciseDetailUi(
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    description = description,
    reps = reps,
    restSeconds = restSeconds,
    notes = notes,
    totalSets = totalSets,
    completedSets = progress.sets.completedSets,
    progressPercentage = progress.sets.progressPercentage,
    isFinished = progress.sets.isFinished,
    weightInGrams = progress.weightInGrams,
    canDecreaseWeight = progress.canDecreaseWeight
)
