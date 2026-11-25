package com.mateusavila.gymtracker.ui.detail

import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.testutil.FakeWorkoutRepository
import com.mateusavila.gymtracker.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository()

    private fun ExerciseDetailViewModel.exercise() =
        (uiState.value as ExerciseDetailUiState.Success).exercise

    @Test
    fun startsLoadingAndThenShowsTheExerciseOfTheId() = runTest {
        val viewModel = ExerciseDetailViewModel("ex1", repository)

        assertEquals(ExerciseDetailUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        val exercise = viewModel.exercise()
        assertEquals(4, exercise.totalSets)
        assertEquals(40_000, exercise.weightInGrams)
        assertEquals("Escápulas retraídas", exercise.notes)
    }

    @Test
    fun bodyweightExerciseHasNoWeightAndNoNotes() = runTest {
        val viewModel = ExerciseDetailViewModel("ex2", repository)

        advanceUntilIdle()

        assertNull(viewModel.exercise().weightInGrams)
        assertNull(viewModel.exercise().notes)
    }

    @Test
    fun unknownIdShowsNotFoundErrorWithoutRetry() = runTest {
        val viewModel = ExerciseDetailViewModel("unknown", repository)

        advanceUntilIdle()

        assertEquals(
            ExerciseDetailUiState.Error(R.string.exercise_not_found, canRetry = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun readFailureShowsErrorWithRetry() = runTest {
        repository.workoutError = IOException()
        val viewModel = ExerciseDetailViewModel("ex1", repository)

        advanceUntilIdle()

        assertEquals(
            ExerciseDetailUiState.Error(R.string.error_reading_workout, canRetry = true),
            viewModel.uiState.value
        )
    }

    @Test
    fun completeAndUndoSetChangeTheProgress() = runTest {
        val viewModel = ExerciseDetailViewModel("ex1", repository)
        advanceUntilIdle()

        viewModel.onCompleteSet()
        viewModel.onCompleteSet()
        viewModel.onUndoSet()
        advanceUntilIdle()

        assertEquals(1, viewModel.exercise().completedSets)
        assertEquals(25, viewModel.exercise().progressPercentage)
        // The change is in the shared repository, so the home list sees it too.
        assertEquals(1, repository.progress.value.getValue("ex1").sets.completedSets)
    }

    @Test
    fun tappingTheLastSetFinishesTheExercise() = runTest {
        val viewModel = ExerciseDetailViewModel("ex1", repository)
        advanceUntilIdle()

        viewModel.onSetClick(4)
        advanceUntilIdle()

        assertTrue(viewModel.exercise().isFinished)
    }

    @Test
    fun weightChangesInStepsOfGrams() = runTest {
        val viewModel = ExerciseDetailViewModel("ex1", repository)
        advanceUntilIdle()

        viewModel.onIncreaseWeight()
        viewModel.onIncreaseWeight()
        viewModel.onDecreaseWeight()
        advanceUntilIdle()

        assertEquals(42_500, viewModel.exercise().weightInGrams)
    }
}
