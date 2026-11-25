package com.mateusavila.gymtracker.data.dto

import kotlinx.serialization.Serializable

/**
 * An exercise as it is written in the JSON file.
 * The optional fields default to null, so the JSON may also leave them out.
 */
@Serializable
data class ExerciseDto(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val totalSets: Int,
    val reps: Int,
    val restSeconds: Int,
    val weightInGrams: Int? = null,
    val notes: String? = null
)
