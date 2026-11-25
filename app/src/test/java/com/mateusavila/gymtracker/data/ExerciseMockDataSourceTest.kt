package com.mateusavila.gymtracker.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExerciseMockDataSourceTest {

    @Test
    fun getExercises_haveUniqueIds() {
        val ids = ExerciseMockDataSource.getExercises().map { it.id }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun findById_returnsTheMatchingExercise() {
        assertEquals("Supino reto", ExerciseMockDataSource.findById("ex1")?.name)
    }

    @Test
    fun findById_returnsNullForUnknownId() {
        assertNull(ExerciseMockDataSource.findById("unknown"))
    }
}
