package com.mateusavila.gymtracker.ui.home

import com.mateusavila.gymtracker.domain.model.Exercise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeUiModelsTest {

    private fun createExercise(
        id: String = "ex-test",
        muscleGroup: String = "Peito",
        totalSets: Int = 4,
        weightInGrams: Int? = 40_000
    ) = Exercise(
        id = id,
        name = "Supino reto",
        muscleGroup = muscleGroup,
        equipment = "Barra",
        description = "Descrição de teste",
        totalSets = totalSets,
        reps = 10,
        restSeconds = 90,
        weightInGrams = weightInGrams,
        notes = null
    )

    @Test
    fun toListItem_mapsOnlyTheFieldsShownInTheList() {
        val item = createExercise().toListItem(order = 2)

        assertEquals(
            ExerciseListItem(
                id = "ex-test",
                order = 2,
                name = "Supino reto",
                muscleGroup = "Peito",
                totalSets = 4,
                reps = 10,
                weightInGrams = 40_000
            ),
            item
        )
    }

    @Test
    fun toListItem_keepsBodyweightAsNull() {
        assertNull(createExercise(weightInGrams = null).toListItem(order = 1).weightInGrams)
    }

    @Test
    fun toWorkoutSummary_countsExercisesSetsAndDistinctMuscleGroups() {
        val summary = listOf(
            createExercise(id = "a", muscleGroup = "Peito", totalSets = 4),
            createExercise(id = "b", muscleGroup = "Pernas", totalSets = 3),
            createExercise(id = "c", muscleGroup = "Peito", totalSets = 3)
        ).toWorkoutSummary()

        assertEquals(WorkoutSummary(exerciseCount = 3, totalSets = 10, muscleGroupCount = 2), summary)
    }
}
