package com.mateusavila.gymtracker.ui.home

import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.SetsProgress
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.startProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiModelsTest {

    private fun createExercise(
        id: String = "ex-test",
        muscleGroup: String = "Peito",
        totalSets: Int = 4,
        weightInGrams: Int? = 40_000
    ) = Exercise(
        id = id,
        name = "Supino reto",
        muscleGroup = muscleGroup,
        equipment = "Barra",
        description = "Descrição de teste",
        totalSets = totalSets,
        reps = 10,
        restSeconds = 90,
        weightInGrams = weightInGrams,
        notes = null
    )

    @Test
    fun toListItem_usesTheProgressSetsAndWeight() {
        val progress = ExerciseProgress(SetsProgress(totalSets = 4, completedSets = 2), weightInGrams = 42_500)

        val item = createExercise().toListItem(order = 2, progress = progress)

        assertEquals(
            ExerciseListItem(
                id = "ex-test",
                order = 2,
                name = "Supino reto",
                muscleGroup = "Peito",
                totalSets = 4,
                reps = 10,
                weightInGrams = 42_500,
                completedSets = 2,
                progressPercentage = 50,
                isFinished = false
            ),
            item
        )
    }

    @Test
    fun toListItem_keepsBodyweightAsNull() {
        val exercise = createExercise(weightInGrams = null)

        assertNull(exercise.toListItem(order = 1, progress = exercise.startProgress()).weightInGrams)
    }

    @Test
    fun toWorkoutSummary_countsExercisesSetsAndDistinctMuscleGroups() {
        val exercises = listOf(
            createExercise(id = "a", muscleGroup = "Peito", totalSets = 4),
            createExercise(id = "b", muscleGroup = "Pernas", totalSets = 3),
            createExercise(id = "c", muscleGroup = "Peito", totalSets = 3)
        )
        val progress = mapOf("a" to ExerciseProgress(SetsProgress(totalSets = 4, completedSets = 4), 40_000))

        val summary = exercises.toWorkoutSummary(progress)

        assertEquals(
            WorkoutSummary(
                exerciseCount = 3,
                totalSets = 10,
                completedSets = 4,
                muscleGroupCount = 2,
                progressPercentage = 40
            ),
            summary
        )
    }

    @Test
    fun toHomeSuccessState_allowsFinishingOnlyWithACompletedSet() {
        val exercise = createExercise(id = "a")
        val workout = Workout(name = "Treino A", exercises = listOf(exercise))
        val noSetsDone = mapOf("a" to exercise.startProgress())
        val oneSetDone = mapOf("a" to exercise.startProgress().let { it.copy(sets = it.sets.completeSet()) })

        assertFalse(workout.toHomeSuccessState(noSetsDone, isFinishing = false).canFinishWorkout)
        assertTrue(workout.toHomeSuccessState(oneSetDone, isFinishing = false).canFinishWorkout)
        assertFalse(workout.toHomeSuccessState(oneSetDone, isFinishing = true).canFinishWorkout)
    }
}
