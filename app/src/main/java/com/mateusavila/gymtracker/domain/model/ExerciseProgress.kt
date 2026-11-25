package com.mateusavila.gymtracker.domain.model

/**
 * Progress of one exercise in the workout being done: the sets already done
 * and the weight in use, which the user can adjust during the workout.
 * [weightInGrams] stays null for bodyweight exercises.
 */
data class ExerciseProgress(
    val sets: SetsProgress,
    val weightInGrams: Int?
) {

    val canDecreaseWeight: Boolean
        get() = weightInGrams != null && weightInGrams - WEIGHT_STEP_IN_GRAMS >= MIN_WEIGHT_IN_GRAMS

    /** Bodyweight exercises have no weight to adjust, so they stay the same. */
    fun increaseWeight(): ExerciseProgress =
        if (weightInGrams == null) this else copy(weightInGrams = weightInGrams + WEIGHT_STEP_IN_GRAMS)

    /** Never goes below [MIN_WEIGHT_IN_GRAMS]. */
    fun decreaseWeight(): ExerciseProgress {
        val newWeight = (weightInGrams ?: return this) - WEIGHT_STEP_IN_GRAMS
        return if (newWeight >= MIN_WEIGHT_IN_GRAMS) copy(weightInGrams = newWeight) else this
    }

    companion object {
        /** Weight changes in steps of 2.5 kg, always as whole grams. */
        const val WEIGHT_STEP_IN_GRAMS = 2_500
        const val MIN_WEIGHT_IN_GRAMS = 2_500
    }
}

/** Progress of an exercise that was not started yet: no sets done and the planned weight. */
fun Exercise.startProgress() = ExerciseProgress(
    sets = SetsProgress(totalSets = totalSets),
    weightInGrams = weightInGrams
)

/** Progress of every exercise of the workout, by exercise id. */
fun Workout.startProgress(): Map<String, ExerciseProgress> =
    exercises.associate { exercise -> exercise.id to exercise.startProgress() }

/**
 * Creates the history entry of this workout with the progress made by the user.
 * Bodyweight exercises (null weight) do not count in the total load.
 */
fun Workout.finish(
    progressById: Map<String, ExerciseProgress>,
    sessionId: String,
    finishedAtEpochMillis: Long
): WorkoutSession {
    var completedSets = 0
    var totalLoadInGrams = 0
    for (exercise in exercises) {
        val progress = progressById[exercise.id] ?: exercise.startProgress()
        completedSets += progress.sets.completedSets
        totalLoadInGrams += (progress.weightInGrams ?: 0) * exercise.reps * progress.sets.completedSets
    }
    return WorkoutSession(
        id = sessionId,
        workoutName = name,
        finishedAtEpochMillis = finishedAtEpochMillis,
        completedSets = completedSets,
        totalSets = exercises.sumOf { it.totalSets },
        totalLoadInGrams = totalLoadInGrams
    )
}
