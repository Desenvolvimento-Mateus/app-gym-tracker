package com.mateusavila.gymtracker.data.mapper

import com.mateusavila.gymtracker.data.dto.WorkoutSessionDto
import com.mateusavila.gymtracker.domain.model.WorkoutSession

fun WorkoutSessionDto.toDomain() = WorkoutSession(
    id = id,
    workoutName = workoutName,
    finishedAtEpochMillis = finishedAtEpochMillis,
    completedSets = completedSets,
    totalSets = totalSets,
    totalLoadInGrams = totalLoadInGrams
)

fun WorkoutSession.toDto() = WorkoutSessionDto(
    id = id,
    workoutName = workoutName,
    finishedAtEpochMillis = finishedAtEpochMillis,
    completedSets = completedSets,
    totalSets = totalSets,
    totalLoadInGrams = totalLoadInGrams
)
