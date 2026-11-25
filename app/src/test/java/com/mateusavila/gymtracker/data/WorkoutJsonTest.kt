package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.data.dto.ExerciseDto
import com.mateusavila.gymtracker.data.dto.WorkoutDto
import com.mateusavila.gymtracker.data.dto.WorkoutSessionDto
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Serialization and deserialization with kotlinx.serialization, using the app Json configuration. */
class WorkoutJsonTest {

    private val exerciseJson = """
        {
          "id": "ex1",
          "name": "Supino reto",
          "muscleGroup": "Peito",
          "equipment": "Barra",
          "description": "Descrição",
          "totalSets": 4,
          "reps": 10,
          "restSeconds": 90,
          "weightInGrams": 12500,
          "notes": "Escápulas retraídas"
        }
    """.trimIndent()

    @Test
    fun decode_readsEveryField() {
        val dto = workoutJson.decodeFromString<ExerciseDto>(exerciseJson)

        assertEquals("ex1", dto.id)
        assertEquals(4, dto.totalSets)
        assertEquals(12_500, dto.weightInGrams)
        assertEquals("Escápulas retraídas", dto.notes)
    }

    @Test
    fun decode_acceptsExplicitNullOptionalFields() {
        val json = exerciseJson
            .replace("\"weightInGrams\": 12500", "\"weightInGrams\": null")
            .replace("\"notes\": \"Escápulas retraídas\"", "\"notes\": null")

        val dto = workoutJson.decodeFromString<ExerciseDto>(json)

        assertNull(dto.weightInGrams)
        assertNull(dto.notes)
    }

    @Test
    fun decode_treatsMissingOptionalFieldsAsNull() {
        val json = """
            {"id": "ex8", "name": "Prancha", "muscleGroup": "Core", "equipment": "Peso corporal",
             "description": "Descrição", "totalSets": 3, "reps": 1, "restSeconds": 45}
        """.trimIndent()

        val dto = workoutJson.decodeFromString<ExerciseDto>(json)

        assertNull(dto.weightInGrams)
        assertNull(dto.notes)
    }

    @Test
    fun decode_ignoresUnknownKeys() {
        val json = exerciseJson.replace("\"id\": \"ex1\",", "\"id\": \"ex1\", \"color\": \"red\",")

        assertEquals("ex1", workoutJson.decodeFromString<ExerciseDto>(json).id)
    }

    @Test
    fun decode_failsWithSerializationExceptionForInvalidJson() {
        assertThrows(SerializationException::class.java) {
            workoutJson.decodeFromString<WorkoutDto>("{ not json")
        }
    }

    @Test
    fun decode_failsWhenARequiredFieldIsMissing() {
        val json = exerciseJson.replace("\"totalSets\": 4,", "")

        assertThrows(SerializationException::class.java) {
            workoutJson.decodeFromString<ExerciseDto>(json)
        }
    }

    @Test
    fun decode_failsWhenTheWeightIsNotAnInteger() {
        // The weight is always a whole number of grams, never a decimal.
        val json = exerciseJson.replace("\"weightInGrams\": 12500", "\"weightInGrams\": 12.5")

        assertThrows(SerializationException::class.java) {
            workoutJson.decodeFromString<ExerciseDto>(json)
        }
    }

    @Test
    fun sessions_surviveEncodeAndDecode() {
        val sessions = listOf(
            WorkoutSessionDto("s2", "Treino A", 2_000L, completedSets = 10, totalSets = 28, totalLoadInGrams = 1_250_000),
            WorkoutSessionDto("s1", "Treino A", 1_000L, completedSets = 28, totalSets = 28, totalLoadInGrams = 4_520_000)
        )

        val text = workoutJson.encodeToString(sessions)

        assertTrue(text.contains("\"totalLoadInGrams\":1250000"))
        assertEquals(sessions, workoutJson.decodeFromString<List<WorkoutSessionDto>>(text))
    }

    @Test
    fun bundledWorkoutFile_isValid() {
        // Unit tests run in the app module folder, so the asset can be read as a normal file.
        val text = File("src/main/assets/workout.json").readText()

        val workout = workoutJson.decodeFromString<WorkoutDto>(text)

        assertEquals(8, workout.exercises.size)
        assertEquals(workout.exercises.size, workout.exercises.map { it.id }.distinct().size)
        assertNull(workout.exercises.first { it.id == "ex8" }.weightInGrams)
    }
}
