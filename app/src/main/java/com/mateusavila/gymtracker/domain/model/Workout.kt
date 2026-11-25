package com.mateusavila.gymtracker.domain.model

/** Today's workout: a name and the exercises in the order they should be done. */
data class Workout(
    val name: String,
    val exercises: List<Exercise>
)
