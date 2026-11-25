package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.domain.model.Workout
import com.mateusavila.gymtracker.domain.model.WorkoutSession
import kotlinx.coroutines.flow.StateFlow

/**
 * Single entry point of the data used by the ViewModels.
 * It is an interface so the ViewModel tests can use a fake implementation.
 */
interface WorkoutRepository {

    /** Progress of the workout being done, by exercise id. Shared by the list and the detail screens. */
    val progress: StateFlow<Map<String, ExerciseProgress>>

    /** Today's workout. Throws IOException or SerializationException when the file cannot be used. */
    suspend fun getWorkout(): Workout

    /** The exercise with this id, or null when the workout has no such exercise. */
    suspend fun getExercise(exerciseId: String): Exercise?

    /** Changes the progress of one exercise, e.g. updateProgress(id) { it.increaseWeight() }. */
    fun updateProgress(exerciseId: String, change: (ExerciseProgress) -> ExerciseProgress)

    /** Saves the current workout in the history and starts the progress from zero again. */
    suspend fun finishWorkout(): WorkoutSession

    /** Finished workouts, most recent first. Empty when no workout was finished yet. */
    suspend fun getHistory(): List<WorkoutSession>
}
