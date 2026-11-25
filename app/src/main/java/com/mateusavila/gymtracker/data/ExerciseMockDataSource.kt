package com.mateusavila.gymtracker.data

import com.mateusavila.gymtracker.domain.model.Exercise

/**
 * Mocked data of today's workout. There is no API or database in this stage.
 */
object ExerciseMockDataSource {

    private val exercises = listOf(
        Exercise(
            id = "ex1",
            name = "Supino reto",
            muscleGroup = "Peito",
            equipment = "Barra",
            description = "Deitado no banco, segure a barra com as mãos um pouco além da largura dos ombros. Desça a barra de forma controlada até encostar levemente no peito e empurre de volta até estender os braços.",
            totalSets = 4,
            reps = 10,
            restSeconds = 90,
            weightInGrams = 40_000,
            notes = "Escápulas retraídas"
        ),
        Exercise(
            id = "ex2",
            name = "Agachamento livre",
            muscleGroup = "Pernas",
            equipment = "Barra",
            description = "Com a barra apoiada no trapézio e os pés na largura dos ombros, desça flexionando o quadril e os joelhos como se fosse sentar. Mantenha o peito aberto e a coluna neutra e suba empurrando o chão com os calcanhares.",
            totalSets = 4,
            reps = 8,
            restSeconds = 120,
            weightInGrams = 60_000,
            notes = "Joelhos alinhados com os pés"
        ),
        Exercise(
            id = "ex3",
            name = "Remada curvada",
            muscleGroup = "Costas",
            equipment = "Barra",
            description = "Com os joelhos levemente flexionados, incline o tronco à frente mantendo a coluna reta. Puxe a barra em direção ao umbigo aproximando as escápulas e desça de forma controlada.",
            totalSets = 4,
            reps = 10,
            restSeconds = 90,
            weightInGrams = 35_000,
            notes = null
        ),
        Exercise(
            id = "ex4",
            name = "Desenvolvimento",
            muscleGroup = "Ombros",
            equipment = "Halteres",
            description = "Sentado com as costas apoiadas, segure os halteres na altura dos ombros. Empurre os pesos para cima até quase estender os cotovelos e desça devagar até a posição inicial.",
            totalSets = 3,
            reps = 12,
            restSeconds = 60,
            weightInGrams = 12_500,
            notes = null
        ),
        Exercise(
            id = "ex5",
            name = "Rosca direta",
            muscleGroup = "Bíceps",
            equipment = "Barra W",
            description = "Em pé, segure a barra W com as palmas para cima e os cotovelos junto ao corpo. Flexione os cotovelos levando a barra até a altura do peito e desça de forma controlada.",
            totalSets = 3,
            reps = 12,
            restSeconds = 60,
            weightInGrams = 20_000,
            notes = "Sem balançar o tronco"
        ),
        Exercise(
            id = "ex6",
            name = "Tríceps corda",
            muscleGroup = "Tríceps",
            equipment = "Polia",
            description = "De frente para a polia alta, segure a corda com os cotovelos junto ao corpo. Estenda os cotovelos afastando as pontas da corda no final do movimento e volte devagar.",
            totalSets = 3,
            reps = 12,
            restSeconds = 60,
            weightInGrams = 25_000,
            notes = null
        ),
        Exercise(
            id = "ex7",
            name = "Leg press 45°",
            muscleGroup = "Pernas",
            equipment = "Máquina",
            description = "Sentado na máquina, apoie os pés na plataforma na largura dos ombros. Destrave a plataforma, desça flexionando os joelhos até cerca de 90 graus e empurre de volta sem travar os joelhos.",
            totalSets = 4,
            reps = 12,
            restSeconds = 90,
            weightInGrams = 120_000,
            notes = null
        ),
        Exercise(
            id = "ex8",
            name = "Prancha",
            muscleGroup = "Core",
            equipment = "Peso corporal",
            description = "Apoie os antebraços e as pontas dos pés no chão, mantendo o corpo alinhado da cabeça aos calcanhares. Contraia o abdômen e os glúteos sem deixar o quadril cair.",
            totalSets = 3,
            reps = 1,
            restSeconds = 45,
            weightInGrams = null,
            notes = "Segurar 40 segundos"
        )
    )

    fun getExercises(): List<Exercise> = exercises

    fun findById(id: String): Exercise? = exercises.find { it.id == id }
}
