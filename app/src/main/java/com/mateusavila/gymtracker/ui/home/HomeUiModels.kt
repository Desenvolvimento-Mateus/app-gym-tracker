package com.mateusavila.gymtracker.ui.home

import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.startProgress

/** UI model of a row of the exercise list. Holds only what the row shows. */
data class ExerciseListItem(
    val id: String,
    val order: Int,
    val name: String,
    val muscleGroup: String,
    val totalSets: Int,
    val reps: Int,
    val weightInGrams: Int?,
    val completedSets: Int,
    val progressPercentage: Int,
    val isFinished: Boolean
)

/** UI model of the workout summary shown in the home header. */
data class WorkoutSummary(
    val exerciseCount: Int,
    val totalSets: Int,
    val completedSets: Int,
    val muscleGroupCount: Int,
    val progressPercentage: Int
)

/** The row shows the weight in use, which may have been adjusted in the detail screen. */
fun Exercise.toListItem(order: Int, progress: ExerciseProgress) = ExerciseListItem(
    id = id,
    order = order,
    name = name,
    muscleGroup = muscleGroup,
    totalSets = totalSets,
    reps = reps,
    weightInGrams = progress.weightInGrams,
    completedSets = progress.sets.completedSets,
    progressPercentage = progress.sets.progressPercentage,
    isFinished = progress.sets.isFinished
)

fun List<Exercise>.toWorkoutSummary(progressById: Map<String, ExerciseProgress>): WorkoutSummary {
    val totalSets = sumOf { it.totalSets }
    val completedSets = sumOf { exercise ->
        (progressById[exercise.id] ?: exercise.startProgress()).sets.completedSets
    }
    return WorkoutSummary(
        exerciseCount = size,
        totalSets = totalSets,
        completedSets = completedSets,
        muscleGroupCount = map { it.muscleGroup }.distinct().size,
        progressPercentage = if (totalSets == 0) 0 else completedSets * 100 / totalSets
    )
}
