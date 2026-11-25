package com.mateusavila.gymtracker.ui.history

import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.testutil.FakeWorkoutRepository
import com.mateusavila.gymtracker.testutil.MainDispatcherRule
import com.mateusavila.gymtracker.testutil.createSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startsLoadingAndThenShowsTheSessions() = runTest {
        val repository = FakeWorkoutRepository(
            sessions = listOf(createSession(id = "s2", completedSets = 7), createSession(id = "s1", completedSets = 3))
        )
        val viewModel = HistoryViewModel(repository)

        assertEquals(HistoryUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        val state = viewModel.uiState.value as HistoryUiState.Success
        assertEquals(listOf("s2", "s1"), state.sessions.map { it.id })
        assertEquals(10, state.totalCompletedSets)
    }

    @Test
    fun showsEmptyWhenNoWorkoutWasFinished() = runTest {
        val viewModel = HistoryViewModel(FakeWorkoutRepository(sessions = emptyList()))

        advanceUntilIdle()

        assertEquals(HistoryUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun showsReadErrorWhenTheFileCannotBeRead() = runTest {
        val repository = FakeWorkoutRepository().apply { historyError = IOException() }
        val viewModel = HistoryViewModel(repository)

        advanceUntilIdle()

        assertEquals(HistoryUiState.Error(R.string.error_reading_history), viewModel.uiState.value)
    }

    @Test
    fun showsInvalidDataErrorWhenTheFileIsCorrupted() = runTest {
        val repository = FakeWorkoutRepository().apply { historyError = SerializationException("invalid") }
        val viewModel = HistoryViewModel(repository)

        advanceUntilIdle()

        assertEquals(HistoryUiState.Error(R.string.error_invalid_history), viewModel.uiState.value)
    }

    @Test
    fun retryAfterAnErrorShowsTheSessions() = runTest {
        val repository = FakeWorkoutRepository(sessions = listOf(createSession()))
        repository.historyError = IOException()
        val viewModel = HistoryViewModel(repository)
        advanceUntilIdle()

        repository.historyError = null
        viewModel.loadHistory()
        advanceUntilIdle()

        assertEquals(1, (viewModel.uiState.value as HistoryUiState.Success).sessions.size)
    }
}
