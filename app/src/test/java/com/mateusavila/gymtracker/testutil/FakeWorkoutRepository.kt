package com.mateusavila.gymtracker.testutil

import com.mateusavila.gymtracker.data.WorkoutRepository
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.WorkoutSession
import com.mateusavila.gymtracker.domain.model.finish
import com.mateusavila.gymtracker.domain.model.startProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory repository for the ViewModel tests: no files, no delays.
 * The *Error fields make the next call fail with that exception.
 */
class FakeWorkoutRepository(
    private val workout: Workout = createWorkout(),
    var sessions: List<WorkoutSession> = emptyList()
) : WorkoutRepository {

    var workoutError: Exception? = null
    var historyError: Exception? = null
    var finishError: Exception? = null

    private val _progress = MutableStateFlow<Map<String, ExerciseProgress>>(emptyMap())
    override val progress: StateFlow<Map<String, ExerciseProgress>> = _progress.asStateFlow()

    override suspend fun getWorkout(): Workout {
        workoutError?.let { throw it }
        if (_progress.value.isEmpty()) _progress.value = workout.startProgress()
        return workout
    }

    override suspend fun getExercise(exerciseId: String): Exercise? =
        getWorkout().exercises.find { it.id == exerciseId }

    override fun updateProgress(exerciseId: String, change: (ExerciseProgress) -> ExerciseProgress) {
        val current = _progress.value[exerciseId] ?: return
        _progress.value = _progress.value + (exerciseId to change(current))
    }

    override suspend fun finishWorkout(): WorkoutSession {
        finishError?.let { throw it }
        val session = workout.finish(_progress.value, sessionId = "session-${sessions.size + 1}", finishedAtEpochMillis = 0)
        sessions = listOf(session) + sessions
        _progress.value = workout.startProgress()
        return session
    }

    override suspend fun getHistory(): List<WorkoutSession> {
        historyError?.let { throw it }
        return sessions
    }
}
