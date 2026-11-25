package com.mateusavila.gymtracker.data.dto

import kotlinx.serialization.Serializable

/** A finished workout as it is saved in the history file. */
@Serializable
data class WorkoutSessionDto(
    val id: String,
    val workoutName: String,
    val finishedAtEpochMillis: Long,
    val completedSets: Int,
    val totalSets: Int,
    val totalLoadInGrams: Int
)
