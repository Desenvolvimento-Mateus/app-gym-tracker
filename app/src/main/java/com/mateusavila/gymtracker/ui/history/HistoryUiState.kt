package com.mateusavila.gymtracker.ui.history

import androidx.annotation.StringRes
import com.mateusavila.gymtracker.domain.model.WorkoutSession
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed interface HistoryUiState {

    data object Loading : HistoryUiState

    data class Error(@param:StringRes val messageRes: Int) : HistoryUiState

    /** No workout was finished yet. */
    data object Empty : HistoryUiState

    data class Success(
        val sessions: List<WorkoutSessionItem>,
        val totalCompletedSets: Int
    ) : HistoryUiState
}

/** UI model of a finished workout in the history list. */
data class WorkoutSessionItem(
    val id: String,
    val workoutName: String,
    val date: String,
    val time: String,
    val completedSets: Int,
    val totalSets: Int,
    val progressPercentage: Int,
    val totalLoadInGrams: Int
)

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** [zoneId] is a parameter so the tests can use a fixed time zone. */
fun WorkoutSession.toHistoryItem(zoneId: ZoneId = ZoneId.systemDefault()): WorkoutSessionItem {
    val finishedAt = Instant.ofEpochMilli(finishedAtEpochMillis).atZone(zoneId)
    return WorkoutSessionItem(
        id = id,
        workoutName = workoutName,
        date = dateFormatter.format(finishedAt),
        time = timeFormatter.format(finishedAt),
        completedSets = completedSets,
        totalSets = totalSets,
        progressPercentage = if (totalSets == 0) 0 else completedSets * 100 / totalSets,
        totalLoadInGrams = totalLoadInGrams
    )
}

fun List<WorkoutSession>.toHistoryUiState(zoneId: ZoneId = ZoneId.systemDefault()): HistoryUiState =
    if (isEmpty()) {
        HistoryUiState.Empty
    } else {
        HistoryUiState.Success(
            sessions = map { it.toHistoryItem(zoneId) },
            totalCompletedSets = sumOf { it.completedSets }
        )
    }
