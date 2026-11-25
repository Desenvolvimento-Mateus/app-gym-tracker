package com.mateusavila.gymtracker.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightFormatterTest {

    @Test
    fun formatWeightInKg_showsWholeKilogramsWithoutDecimals() {
        assertEquals("40", formatWeightInKg(40_000))
        assertEquals("120", formatWeightInKg(120_000))
    }

    @Test
    fun formatWeightInKg_showsOnlyTheNeededDecimals() {
        assertEquals("12,5", formatWeightInKg(12_500))
        assertEquals("2,25", formatWeightInKg(2_250))
        assertEquals("1,005", formatWeightInKg(1_005))
    }

    @Test
    fun formatWeightInKg_groupsThousandsWithADot() {
        assertEquals("4.520", formatWeightInKg(4_520_000))
        assertEquals("1.250,5", formatWeightInKg(1_250_500))
    }

    @Test
    fun formatWeightInKg_handlesWeightsBelowOneKilogram() {
        assertEquals("0,5", formatWeightInKg(500))
    }
}
