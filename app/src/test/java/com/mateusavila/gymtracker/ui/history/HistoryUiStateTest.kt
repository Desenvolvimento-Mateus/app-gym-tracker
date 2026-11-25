package com.mateusavila.gymtracker.ui.history

import com.mateusavila.gymtracker.domain.model.WorkoutSession
import com.mateusavila.gymtracker.testutil.createSession
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class HistoryUiStateTest {

    @Test
    fun toHistoryItem_formatsDateAndTimeInTheGivenZone() {
        val finishedAt = LocalDateTime.of(2026, 11, 25, 18, 40).toInstant(ZoneOffset.UTC).toEpochMilli()

        val item = createSession(finishedAtEpochMillis = finishedAt).toHistoryItem(ZoneOffset.UTC)

        assertEquals("25/11/2026", item.date)
        assertEquals("18:40", item.time)
    }

    @Test
    fun toHistoryItem_calculatesTheProgressPercentage() {
        val item = createSession(completedSets = 7, totalSets = 28).toHistoryItem(ZoneOffset.UTC)

        assertEquals(25, item.progressPercentage)
    }

    @Test
    fun toHistoryUiState_isEmptyWithoutSessions() {
        assertEquals(HistoryUiState.Empty, emptyList<WorkoutSession>().toHistoryUiState())
    }
}
