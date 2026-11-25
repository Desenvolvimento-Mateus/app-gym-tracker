package com.mateusavila.gymtracker.domain.model

import com.mateusavila.gymtracker.testutil.createExercise
import com.mateusavila.gymtracker.testutil.createWorkout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseProgressTest {

    private fun progress(weightInGrams: Int?, completedSets: Int = 0) =
        ExerciseProgress(SetsProgress(totalSets = 4, completedSets = completedSets), weightInGrams)

    @Test
    fun startProgress_hasNoSetsDoneAndThePlannedWeight() {
        val progress = createExercise(totalSets = 4, weightInGrams = 40_000).startProgress()

        assertEquals(0, progress.sets.completedSets)
        assertEquals(4, progress.sets.totalSets)
        assertEquals(40_000, progress.weightInGrams)
    }

    @Test
    fun increaseWeight_addsOneStepInGrams() {
        assertEquals(42_500, progress(weightInGrams = 40_000).increaseWeight().weightInGrams)
    }

    @Test
    fun decreaseWeight_removesOneStepInGrams() {
        assertEquals(37_500, progress(weightInGrams = 40_000).decreaseWeight().weightInGrams)
    }

    @Test
    fun decreaseWeight_neverGoesBelowTheMinimum() {
        val progress = progress(weightInGrams = ExerciseProgress.MIN_WEIGHT_IN_GRAMS)

        assertFalse(progress.canDecreaseWeight)
        assertSame(progress, progress.decreaseWeight())
    }

    @Test
    fun bodyweightExercise_keepsNullWeight() {
        val progress = progress(weightInGrams = null)

        assertFalse(progress.canDecreaseWeight)
        assertNull(progress.increaseWeight().weightInGrams)
        assertNull(progress.decreaseWeight().weightInGrams)
    }

    @Test
    fun canDecreaseWeight_isTrueAboveTheMinimum() {
        assertTrue(progress(weightInGrams = 5_000).canDecreaseWeight)
    }

    @Test
    fun finish_sumsCompletedSetsAndLoadIgnoringBodyweight() {
        val workout = createWorkout(
            exercises = listOf(
                createExercise(id = "a", totalSets = 4, reps = 10, weightInGrams = 40_000),
                createExercise(id = "b", totalSets = 3, reps = 1, weightInGrams = null)
            )
        )
        val progressById = mapOf(
            // Weight adjusted from 40 kg to 42.5 kg, 2 sets of 10 reps.
            "a" to ExerciseProgress(SetsProgress(totalSets = 4, completedSets = 2), weightInGrams = 42_500),
            "b" to ExerciseProgress(SetsProgress(totalSets = 3, completedSets = 3), weightInGrams = null)
        )

        val session = workout.finish(progressById, sessionId = "s1", finishedAtEpochMillis = 123L)

        assertEquals(
            WorkoutSession(
                id = "s1",
                workoutName = "Treino A",
                finishedAtEpochMillis = 123L,
                completedSets = 5,
                totalSets = 7,
                totalLoadInGrams = 42_500 * 10 * 2
            ),
            session
        )
    }

    @Test
    fun finish_treatsMissingProgressAsNotStarted() {
        val workout = createWorkout(exercises = listOf(createExercise(id = "a")))

        val session = workout.finish(emptyMap(), sessionId = "s1", finishedAtEpochMillis = 0L)

        assertEquals(0, session.completedSets)
        assertEquals(0, session.totalLoadInGrams)
    }
}
