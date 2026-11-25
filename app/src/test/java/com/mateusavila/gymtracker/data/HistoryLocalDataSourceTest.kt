package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.data.dto.WorkoutSessionDto
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class HistoryLocalDataSourceTest {

    // Creates a folder for each test and deletes it at the end, like context.filesDir.
    @get:Rule
    val folder = TemporaryFolder()

    private fun historyFile() = File(folder.root, "workout_history.json")

    @Test
    fun readSessions_returnsEmptyListWhenTheFileDoesNotExist() = runTest {
        val dataSource = HistoryLocalDataSource(historyFile())

        assertTrue(dataSource.readSessions().isEmpty())
    }

    @Test
    fun saveSessions_thenReadSessions_returnsTheSameSessions() = runTest {
        val dataSource = HistoryLocalDataSource(historyFile())
        val sessions = listOf(
            WorkoutSessionDto("s1", "Treino A", 1_000L, completedSets = 5, totalSets = 28, totalLoadInGrams = 600_000)
        )

        dataSource.saveSessions(sessions)

        assertEquals(sessions, dataSource.readSessions())
        // A new instance (like reopening the app) reads the same file.
        assertEquals(sessions, HistoryLocalDataSource(historyFile()).readSessions())
    }

    @Test
    fun readSessions_failsWhenTheFileIsCorrupted() {
        val file = historyFile().apply { writeText("[{\"id\": ") }
        val dataSource = HistoryLocalDataSource(file)

        assertThrows(SerializationException::class.java) {
            runTest { dataSource.readSessions() }
        }
    }
}
