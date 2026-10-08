package com.mateusavila.gymtracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ExerciseDetailTest {

    private fun createExercise(completedSets: Int = 0, totalSets: Int = 4) = ExerciseDetail(
        id = "ex-test",
        name = "Supino reto",
        muscleGroup = "Peito",
        equipment = "Barra",
        description = "Descrição de teste",
        reps = 10,
        restSeconds = 90,
        weightKg = 40.0,
        notes = null,
        completedSets = completedSets,
        totalSets = totalSets
    )

    @Test
    fun completeSet_incrementsCompletedSets() {
        val exercise = createExercise(completedSets = 1)

        val result = exercise.completeSet()

        assertEquals(2, result?.completedSets)
    }

    @Test
    fun completeSet_doesNotChangeOriginalExercise() {
        val exercise = createExercise(completedSets = 1)

        exercise.completeSet()

        assertEquals(1, exercise.completedSets)
    }

    @Test
    fun completeSet_returnsNullWhenAllSetsAreCompleted() {
        val exercise = createExercise(completedSets = 4, totalSets = 4)

        assertNull(exercise.completeSet())
    }

    @Test
    fun undoSet_decrementsCompletedSets() {
        val exercise = createExercise(completedSets = 2)

        val result = exercise.undoSet()

        assertEquals(1, result?.completedSets)
    }

    @Test
    fun undoSet_returnsNullWhenNoSetIsCompleted() {
        val exercise = createExercise(completedSets = 0)

        assertNull(exercise.undoSet())
    }

    @Test
    fun progressPercentage_isCalculatedFromCompletedSets() {
        assertEquals(0, createExercise(completedSets = 0).progressPercentage)
        assertEquals(50, createExercise(completedSets = 2).progressPercentage)
        assertEquals(100, createExercise(completedSets = 4).progressPercentage)
    }

    @Test
    fun progressPercentage_truncatesDecimals() {
        assertEquals(33, createExercise(completedSets = 1, totalSets = 3).progressPercentage)
    }

    @Test
    fun init_throwsWhenTotalSetsIsZero() {
        assertThrows(IllegalArgumentException::class.java) {
            createExercise(completedSets = 0, totalSets = 0)
        }
    }

    @Test
    fun init_throwsWhenCompletedSetsIsNegative() {
        assertThrows(IllegalArgumentException::class.java) {
            createExercise(completedSets = -1)
        }
    }

    @Test
    fun init_throwsWhenCompletedSetsIsGreaterThanTotalSets() {
        assertThrows(IllegalArgumentException::class.java) {
            createExercise(completedSets = 5, totalSets = 4)
        }
    }

    @Test
    fun mockExercises_haveUniqueIds() {
        val ids = mockExercises.map { it.id }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun mockExercises_startWithNoCompletedSets() {
        mockExercises.forEach {
            assertEquals(0, it.completedSets)
        }
    }
}
