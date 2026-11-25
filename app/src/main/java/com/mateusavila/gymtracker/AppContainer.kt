package com.mateusavila.gymtracker

import android.content.Context
import com.mateusavila.gymtracker.data.DefaultWorkoutRepository
import com.mateusavila.gymtracker.data.HistoryLocalDataSource
import com.mateusavila.gymtracker.data.WorkoutLocalDataSource
import com.mateusavila.gymtracker.data.WorkoutRepository
import java.io.File

/**
 * Manual dependency injection: creates the objects the app needs in one place.
 * There is a single instance, kept by GymTrackerApplication, so every screen
 * shares the same repository (and the same workout progress).
 */
class AppContainer(context: Context) {

    val workoutRepository: WorkoutRepository = DefaultWorkoutRepository(
        workoutDataSource = WorkoutLocalDataSource(context.assets),
        historyDataSource = HistoryLocalDataSource(File(context.filesDir, HISTORY_FILE_NAME))
    )

    private companion object {
        const val HISTORY_FILE_NAME = "workout_history.json"
    }
}
