package com.mateusavila.gymtracker.data

import android.content.res.AssetManager
import com.mateusavila.gymtracker.data.dto.WorkoutDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/** Reads today's workout from assets/workout.json. */
class WorkoutLocalDataSource(
    private val assetManager: AssetManager,
    private val json: Json = workoutJson
) {

    /**
     * Throws IOException if the file cannot be read and
     * SerializationException if its content is not a valid workout.
     */
    suspend fun readWorkout(): WorkoutDto = withContext(Dispatchers.IO) {
        val text = assetManager.open(WORKOUT_FILE_NAME).bufferedReader().use { it.readText() }
        json.decodeFromString<WorkoutDto>(text)
    }

    private companion object {
        const val WORKOUT_FILE_NAME = "workout.json"
    }
}
