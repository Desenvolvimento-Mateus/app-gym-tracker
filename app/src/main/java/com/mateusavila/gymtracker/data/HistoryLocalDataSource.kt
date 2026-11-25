package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.data.dto.WorkoutSessionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Reads and writes the finished workouts as a JSON list in [historyFile]
 * (a file in the app internal storage, context.filesDir).
 */
class HistoryLocalDataSource(
    private val historyFile: File,
    private val json: Json = workoutJson
) {

    suspend fun readSessions(): List<WorkoutSessionDto> = withContext(Dispatchers.IO) {
        // No workout was finished yet: a missing file means an empty history, not an error.
        if (!historyFile.exists()) return@withContext emptyList()
        json.decodeFromString<List<WorkoutSessionDto>>(historyFile.readText())
    }

    suspend fun saveSessions(sessions: List<WorkoutSessionDto>) = withContext(Dispatchers.IO) {
        historyFile.writeText(json.encodeToString(sessions))
    }
}
