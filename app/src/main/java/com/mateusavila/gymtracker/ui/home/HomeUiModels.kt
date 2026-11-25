package com.mateusavila.gymtracker.ui.home

import com.mateusavila.gymtracker.domain.model.Exercise

/** UI model of a row of the exercise list. Holds only what the row shows. */
data class ExerciseListItem(
    val id: String,
    val order: Int,
    val name: String,
    val muscleGroup: String,
    val totalSets: Int,
    val reps: Int,
    val weightInGrams: Int?
)

/** UI model of the workout summary shown in the home header. */
data class WorkoutSummary(
    val exerciseCount: Int,
    val totalSets: Int,
    val muscleGroupCount: Int
)

fun Exercise.toListItem(order: Int) = ExerciseListItem(
    id = id,
    order = order,
    name = name,
    muscleGroup = muscleGroup,
    totalSets = totalSets,
    reps = reps,
    weightInGrams = weightInGrams
)

fun List<Exercise>.toWorkoutSummary() = WorkoutSummary(
    exerciseCount = size,
    totalSets = sumOf { it.totalSets },
    muscleGroupCount = map { it.muscleGroup }.distinct().size
)
