package com.mateusavila.gymtracker.testutil

import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.WorkoutSession

fun createExercise(
    id: String = "ex1",
    muscleGroup: String = "Peito",
    totalSets: Int = 4,
    reps: Int = 10,
    weightInGrams: Int? = 40_000,
    notes: String? = null
) = Exercise(
    id = id,
    name = "Supino reto",
    muscleGroup = muscleGroup,
    equipment = "Barra",
    description = "Descrição de teste",
    totalSets = totalSets,
    reps = reps,
    restSeconds = 90,
    weightInGrams = weightInGrams,
    notes = notes
)

fun createWorkout(
    exercises: List<Exercise> = listOf(
        createExercise(id = "ex1", weightInGrams = 40_000, notes = "Escápulas retraídas"),
        createExercise(id = "ex2", muscleGroup = "Core", totalSets = 3, reps = 1, weightInGrams = null)
    )
) = Workout(name = "Treino A", exercises = exercises)

fun createSession(
    id: String = "session-1",
    finishedAtEpochMillis: Long = 1_764_000_000_000,
    completedSets: Int = 5,
    totalSets: Int = 7
) = WorkoutSession(
    id = id,
    workoutName = "Treino A",
    finishedAtEpochMillis = finishedAtEpochMillis,
    completedSets = completedSets,
    totalSets = totalSets,
    totalLoadInGrams = 1_600_000
)
