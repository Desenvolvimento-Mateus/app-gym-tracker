package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.data.mapper.toDomain
import com.mateusavila.gymtracker.data.mapper.toDto
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.WorkoutSession
import com.mateusavila.gymtracker.domain.model.finish
import com.mateusavila.gymtracker.domain.model.startProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class DefaultWorkoutRepository(
    private val workoutDataSource: WorkoutLocalDataSource,
    private val historyDataSource: HistoryLocalDataSource
) : WorkoutRepository {

    private val _progress = MutableStateFlow<Map<String, ExerciseProgress>>(emptyMap())
    override val progress: StateFlow<Map<String, ExerciseProgress>> = _progress.asStateFlow()

    // The workout file is read only once and then kept in memory.
    private var cachedWorkout: Workout? = null

    // The home and the detail screens may ask for the workout at the same time:
    // the mutex makes the second call wait for the first one instead of reading the file twice.
    private val mutex = Mutex()

    override suspend fun getWorkout(): Workout = mutex.withLock {
        cachedWorkout ?: loadWorkout().also { cachedWorkout = it }
    }

    private suspend fun loadWorkout(): Workout {
        // Short pause only so the loading state can be seen; the file below is really read.
        delay(SIMULATED_DELAY_MILLIS)
        val workout = workoutDataSource.readWorkout().toDomain()
        _progress.value = workout.startProgress()
        return workout
    }

    override suspend fun getExercise(exerciseId: String): Exercise? =
        getWorkout().exercises.find { it.id == exerciseId }

    override fun updateProgress(exerciseId: String, change: (ExerciseProgress) -> ExerciseProgress) {
        _progress.update { current ->
            val exerciseProgress = current[exerciseId] ?: return@update current
            current + (exerciseId to change(exerciseProgress))
        }
    }

    override suspend fun finishWorkout(): WorkoutSession {
        val workout = getWorkout()
        val session = workout.finish(
            progressById = _progress.value,
            sessionId = UUID.randomUUID().toString(),
            finishedAtEpochMillis = System.currentTimeMillis()
        )
        delay(SIMULATED_DELAY_MILLIS)
        val savedSessions = historyDataSource.readSessions()
        historyDataSource.saveSessions(listOf(session.toDto()) + savedSessions)

        // The workout was saved, so the next one starts from zero.
        _progress.value = workout.startProgress()
        return session
    }

    override suspend fun getHistory(): List<WorkoutSession> {
        delay(SIMULATED_DELAY_MILLIS)
        return historyDataSource.readSessions()
            .map { it.toDomain() }
            .sortedByDescending { it.finishedAtEpochMillis }
    }

    private companion object {
        const val SIMULATED_DELAY_MILLIS = 600L
    }
}
