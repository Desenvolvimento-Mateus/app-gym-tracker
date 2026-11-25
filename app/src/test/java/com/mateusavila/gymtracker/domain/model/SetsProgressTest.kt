package com.mateusavila.gymtracker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SetsProgressTest {

    @Test
    fun completeSet_incrementsCompletedSets() {
        val progress = SetsProgress(totalSets = 4, completedSets = 1)

        assertEquals(2, progress.completeSet().completedSets)
    }

    @Test
    fun completeSet_doesNotChangeOriginalInstance() {
        val progress = SetsProgress(totalSets = 4, completedSets = 1)

        progress.completeSet()

        assertEquals(1, progress.completedSets)
    }

    @Test
    fun completeSet_keepsSameInstanceWhenFinished() {
        val progress = SetsProgress(totalSets = 4, completedSets = 4)

        assertSame(progress, progress.completeSet())
    }

    @Test
    fun undoSet_decrementsCompletedSets() {
        val progress = SetsProgress(totalSets = 4, completedSets = 2)

        assertEquals(1, progress.undoSet().completedSets)
    }

    @Test
    fun undoSet_keepsSameInstanceWhenNoSetIsCompleted() {
        val progress = SetsProgress(totalSets = 4)

        assertSame(progress, progress.undoSet())
    }

    @Test
    fun toggleSet_completesEverySetUpToTheTappedOne() {
        val progress = SetsProgress(totalSets = 4, completedSets = 1)

        assertEquals(3, progress.toggleSet(3).completedSets)
    }

    @Test
    fun toggleSet_undoesTheTappedSetAndTheFollowingOnes() {
        val progress = SetsProgress(totalSets = 4, completedSets = 3)

        assertEquals(1, progress.toggleSet(2).completedSets)
    }

    @Test
    fun toggleSet_throwsWhenSetNumberIsOutOfRange() {
        val progress = SetsProgress(totalSets = 4)

        assertThrows(IllegalArgumentException::class.java) { progress.toggleSet(0) }
        assertThrows(IllegalArgumentException::class.java) { progress.toggleSet(5) }
    }

    @Test
    fun isFinished_isTrueOnlyWhenAllSetsAreCompleted() {
        assertFalse(SetsProgress(totalSets = 3, completedSets = 2).isFinished)
        assertTrue(SetsProgress(totalSets = 3, completedSets = 3).isFinished)
    }

    @Test
    fun progressPercentage_isCalculatedFromCompletedSets() {
        assertEquals(0, SetsProgress(totalSets = 4).progressPercentage)
        assertEquals(50, SetsProgress(totalSets = 4, completedSets = 2).progressPercentage)
        assertEquals(100, SetsProgress(totalSets = 4, completedSets = 4).progressPercentage)
    }

    @Test
    fun progressPercentage_truncatesDecimals() {
        assertEquals(33, SetsProgress(totalSets = 3, completedSets = 1).progressPercentage)
    }

    @Test
    fun init_throwsWhenValuesAreInvalid() {
        assertThrows(IllegalArgumentException::class.java) { SetsProgress(totalSets = 0) }
        assertThrows(IllegalArgumentException::class.java) { SetsProgress(totalSets = 4, completedSets = -1) }
        assertThrows(IllegalArgumentException::class.java) { SetsProgress(totalSets = 4, completedSets = 5) }
    }
}
