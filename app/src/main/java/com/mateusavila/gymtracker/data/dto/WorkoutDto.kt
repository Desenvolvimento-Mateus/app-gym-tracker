package com.mateusavila.gymtracker.data.dto

import kotlinx.serialization.Serializable

/** Shape of assets/workout.json. Only used by the data layer. */
@Serializable
data class WorkoutDto(
    val name: String,
    val exercises: List<ExerciseDto>
)
