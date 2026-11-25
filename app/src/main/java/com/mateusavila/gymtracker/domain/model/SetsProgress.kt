package com.mateusavila.gymtracker.domain.model

/**
 * Immutable progress of the sets of an exercise during the workout.
 * Every change returns a new instance through copy().
 */
data class SetsProgress(
    val totalSets: Int,
    val completedSets: Int = 0
) {
    init {
        require(totalSets > 0) { "totalSets must be greater than zero" }
        require(completedSets in 0..totalSets) { "completedSets must be between 0 and totalSets" }
    }

    val isFinished: Boolean
        get() = completedSets == totalSets

    val progressPercentage: Int
        get() = completedSets * 100 / totalSets

    fun completeSet(): SetsProgress =
        if (isFinished) this else copy(completedSets = completedSets + 1)

    fun undoSet(): SetsProgress =
        if (completedSets == 0) this else copy(completedSets = completedSets - 1)

    /**
     * Tapping a set that is still pending completes every set up to it.
     * Tapping a set that is already done undoes it and every set after it.
     */
    fun toggleSet(setNumber: Int): SetsProgress {
        require(setNumber in 1..totalSets) { "setNumber must be between 1 and totalSets" }
        val newCompletedSets = if (setNumber <= completedSets) setNumber - 1 else setNumber
        return copy(completedSets = newCompletedSets)
    }
}
