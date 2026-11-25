package com.mateusavila.gymtracker.domain.model

/**
 * Domain model of an exercise in the workout.
 *
 * [weightInGrams] is null for bodyweight exercises. The weight is stored as an Int in grams
 * to avoid floating point values (e.g. 12.5 kg -> 12500).
 * [notes] is null when the exercise has no extra tip.
 */
data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val totalSets: Int,
    val reps: Int,
    val restSeconds: Int,
    val weightInGrams: Int?,
    val notes: String?
) {
    init {
        require(totalSets > 0) { "totalSets must be greater than zero" }
        require(reps > 0) { "reps must be greater than zero" }
        require(restSeconds >= 0) { "restSeconds must not be negative" }
        require(weightInGrams == null || weightInGrams > 0) { "weightInGrams must be greater than zero" }
    }
}
