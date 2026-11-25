package com.mateusavila.gymtracker.data.mapper

import com.mateusavila.gymtracker.data.dto.ExerciseDto
import com.mateusavila.gymtracker.data.dto.WorkoutDto
import com.mateusavila.gymtracker.data.dto.WorkoutSessionDto
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.testutil.createSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class WorkoutMappersTest {

    private fun createDto(
        totalSets: Int = 4,
        weightInGrams: Int? = 40_000,
        notes: String? = "Escápulas retraídas"
    ) = ExerciseDto(
        id = "ex1",
        name = "Supino reto",
        muscleGroup = "Peito",
        equipment = "Barra",
        description = "Descrição",
        totalSets = totalSets,
        reps = 10,
        restSeconds = 90,
        weightInGrams = weightInGrams,
        notes = notes
    )

    @Test
    fun exerciseDtoToDomain_copiesEveryField() {
        assertEquals(
            Exercise(
                id = "ex1",
                name = "Supino reto",
                muscleGroup = "Peito",
                equipment = "Barra",
                description = "Descrição",
                totalSets = 4,
                reps = 10,
                restSeconds = 90,
                weightInGrams = 40_000,
                notes = "Escápulas retraídas"
            ),
            createDto().toDomain()
        )
    }

    @Test
    fun exerciseDtoToDomain_keepsOptionalFieldsNull() {
        val exercise = createDto(weightInGrams = null, notes = null).toDomain()

        assertNull(exercise.weightInGrams)
        assertNull(exercise.notes)
    }

    @Test
    fun exerciseDtoToDomain_rejectsInvalidValues() {
        assertThrows(IllegalArgumentException::class.java) { createDto(totalSets = 0).toDomain() }
        assertThrows(IllegalArgumentException::class.java) { createDto(weightInGrams = -1).toDomain() }
    }

    @Test
    fun workoutDtoToDomain_keepsTheExerciseOrder() {
        val dto = WorkoutDto(
            name = "Treino A",
            exercises = listOf(createDto().copy(id = "b"), createDto().copy(id = "a"))
        )

        val workout = dto.toDomain()

        assertEquals("Treino A", workout.name)
        assertEquals(listOf("b", "a"), workout.exercises.map { it.id })
    }

    @Test
    fun workoutSession_mapsToDtoAndBackWithoutChanges() {
        val session = createSession()

        val dto = session.toDto()

        assertEquals(
            WorkoutSessionDto(
                id = session.id,
                workoutName = session.workoutName,
                finishedAtEpochMillis = session.finishedAtEpochMillis,
                completedSets = session.completedSets,
                totalSets = session.totalSets,
                totalLoadInGrams = session.totalLoadInGrams
            ),
            dto
        )
        assertEquals(session, dto.toDomain())
    }
}
