package com.mateusavila.gymtracker.data.mapper

import com.mateusavila.gymtracker.data.dto.ExerciseDto
import com.mateusavila.gymtracker.data.dto.WorkoutDto
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.Workout

// The Exercise constructor validates the values, so an invalid file
// fails here with IllegalArgumentException instead of reaching the UI.
fun ExerciseDto.toDomain() = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    description = description,
    totalSets = totalSets,
    reps = reps,
    restSeconds = restSeconds,
    weightInGrams = weightInGrams,
    notes = notes
)

fun WorkoutDto.toDomain() = Workout(
    name = name,
    exercises = exercises.map { it.toDomain() }
)
