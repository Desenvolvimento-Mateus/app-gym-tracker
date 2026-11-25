package com.mateusavila.gymtracker.ui.home

import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.testutil.FakeWorkoutRepository
import com.mateusavila.gymtracker.testutil.MainDispatcherRule
import com.mateusavila.gymtracker.testutil.createWorkout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun HomeViewModel.successState() = uiState.value as HomeUiState.Success

    @Test
    fun startsLoadingAndThenShowsTheWorkout() = runTest {
        val viewModel = HomeViewModel(FakeWorkoutRepository())

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        val state = viewModel.successState()
        assertEquals("Treino A", state.workoutName)
        assertEquals(listOf("ex1", "ex2"), state.exercises.map { it.id })
        assertEquals(7, state.summary.totalSets)
        assertFalse(state.canFinishWorkout)
    }

    @Test
    fun showsReadErrorWhenTheFileCannotBeRead() = runTest {
        val repository = FakeWorkoutRepository().apply { workoutError = IOException() }
        val viewModel = HomeViewModel(repository)

        advanceUntilIdle()

        assertEquals(HomeUiState.Error(R.string.error_reading_workout), viewModel.uiState.value)
    }

    @Test
    fun showsInvalidDataErrorWhenTheJsonIsInvalid() = runTest {
        val repository = FakeWorkoutRepository().apply { workoutError = SerializationException("invalid") }
        val viewModel = HomeViewModel(repository)

        advanceUntilIdle()

        assertEquals(HomeUiState.Error(R.string.error_invalid_workout), viewModel.uiState.value)
    }

    @Test
    fun retryAfterAnErrorShowsTheWorkout() = runTest {
        val repository = FakeWorkoutRepository().apply { workoutError = IOException() }
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        repository.workoutError = null
        viewModel.loadWorkout()

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
    }

    @Test
    fun showsEmptyWhenTheWorkoutHasNoExercises() = runTest {
        val viewModel = HomeViewModel(FakeWorkoutRepository(workout = createWorkout(exercises = emptyList())))

        advanceUntilIdle()

        assertEquals(HomeUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun progressChangedInTheRepositoryUpdatesTheList() = runTest {
        val repository = FakeWorkoutRepository()
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // Same thing the detail screen does when a set is completed.
        repository.updateProgress("ex1") { it.copy(sets = it.sets.completeSet()) }
        advanceUntilIdle()

        val state = viewModel.successState()
        assertEquals(1, state.exercises.first { it.id == "ex1" }.completedSets)
        assertEquals(1, state.summary.completedSets)
        assertTrue(state.canFinishWorkout)
    }

    @Test
    fun finishWorkout_savesTheSessionAndSendsFinishedEvent() = runTest {
        val repository = FakeWorkoutRepository()
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        repository.updateProgress("ex1") { it.copy(sets = it.sets.completeSet()) }

        viewModel.finishWorkout()
        advanceUntilIdle()

        assertEquals(HomeEvent.WorkoutFinished, viewModel.events.first())
        assertEquals(1, repository.sessions.size)
        // The progress starts from zero for the next workout.
        assertEquals(0, viewModel.successState().summary.completedSets)
        assertFalse(viewModel.successState().isFinishing)
    }

    @Test
    fun finishWorkout_sendsFailedEventWhenSavingFails() = runTest {
        val repository = FakeWorkoutRepository().apply { finishError = IOException() }
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        viewModel.finishWorkout()
        advanceUntilIdle()

        assertEquals(HomeEvent.FinishFailed, viewModel.events.first())
        assertTrue(repository.sessions.isEmpty())
        assertFalse(viewModel.successState().isFinishing)
    }
}
