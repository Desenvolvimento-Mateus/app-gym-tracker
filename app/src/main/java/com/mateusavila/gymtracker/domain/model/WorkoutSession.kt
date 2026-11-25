package com.mateusavila.gymtracker.domain.model

/**
 * A finished workout saved in the history.
 * [totalLoadInGrams] is the volume lifted: weight x reps x completed sets of every exercise.
 */
data class WorkoutSession(
    val id: String,
    val workoutName: String,
    val finishedAtEpochMillis: Long,
    val completedSets: Int,
    val totalSets: Int,
    val totalLoadInGrams: Int
)
