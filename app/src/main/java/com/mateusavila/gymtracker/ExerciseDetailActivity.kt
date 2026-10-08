package com.mateusavila.gymtracker

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mateusavila.gymtracker.databinding.BadgeLayoutBinding
import com.mateusavila.gymtracker.databinding.DetailRowLayoutBinding
import com.mateusavila.gymtracker.databinding.ExerciseDetailLayoutBinding

val mockExercises = listOf(
    ExerciseDetail(
        id = "ex1",
        name = "Supino reto",
        muscleGroup = "Peito",
        equipment = "Barra",
        description = "Deitado no banco, segure a barra com as mãos um pouco além da largura dos ombros. Desça a barra de forma controlada até encostar levemente no peito e empurre de volta até estender os braços.",
        reps = 10,
        restSeconds = 90,
        weightKg = 40.0,
        notes = "Escápulas retraídas",
        totalSets = 4
    ),
    ExerciseDetail(
        id = "ex2",
        name = "Agachamento livre",
        muscleGroup = "Pernas",
        equipment = "Barra",
        description = "Com a barra apoiada no trapézio e os pés na largura dos ombros, desça flexionando o quadril e os joelhos como se fosse sentar. Mantenha o peito aberto e a coluna neutra e suba empurrando o chão com os calcanhares.",
        reps = 8,
        restSeconds = 120,
        weightKg = 60.0,
        notes = "Joelhos alinhados com os pés",
        totalSets = 4
    ),
    ExerciseDetail(
        id = "ex3",
        name = "Remada curvada",
        muscleGroup = "Costas",
        equipment = "Barra",
        description = "Com os joelhos levemente flexionados, incline o tronco à frente mantendo a coluna reta. Puxe a barra em direção ao umbigo aproximando as escápulas e desça de forma controlada.",
        reps = 10,
        restSeconds = 90,
        weightKg = 35.0,
        notes = null,
        totalSets = 4
    ),
    ExerciseDetail(
        id = "ex4",
        name = "Desenvolvimento",
        muscleGroup = "Ombros",
        equipment = "Halteres",
        description = "Sentado com as costas apoiadas, segure os halteres na altura dos ombros. Empurre os pesos para cima até quase estender os cotovelos e desça devagar até a posição inicial.",
        reps = 12,
        restSeconds = 60,
        weightKg = 12.0,
        notes = null,
        totalSets = 3
    ),
    ExerciseDetail(
        id = "ex5",
        name = "Rosca direta",
        muscleGroup = "Bíceps",
        equipment = "Barra W",
        description = "Em pé, segure a barra W com as palmas para cima e os cotovelos junto ao corpo. Flexione os cotovelos levando a barra até a altura do peito e desça de forma controlada.",
        reps = 12,
        restSeconds = 60,
        weightKg = 20.0,
        notes = "Sem balançar o tronco",
        totalSets = 3
    ),
    ExerciseDetail(
        id = "ex6",
        name = "Tríceps corda",
        muscleGroup = "Tríceps",
        equipment = "Polia",
        description = "De frente para a polia alta, segure a corda com os cotovelos junto ao corpo. Estenda os cotovelos afastando as pontas da corda no final do movimento e volte devagar.",
        reps = 12,
        restSeconds = 60,
        weightKg = 25.0,
        notes = null,
        totalSets = 3
    ),
    ExerciseDetail(
        id = "ex7",
        name = "Leg press 45°",
        muscleGroup = "Pernas",
        equipment = "Máquina",
        description = "Sentado na máquina, apoie os pés na plataforma na largura dos ombros. Destrave a plataforma, desça flexionando os joelhos até cerca de 90 graus e empurre de volta sem travar os joelhos.",
        reps = 12,
        restSeconds = 90,
        weightKg = 120.0,
        notes = null,
        totalSets = 4
    ),
    ExerciseDetail(
        id = "ex8",
        name = "Prancha",
        muscleGroup = "Core",
        equipment = "Peso corporal",
        description = "Apoie os antebraços e as pontas dos pés no chão, mantendo o corpo alinhado da cabeça aos calcanhares. Contraia o abdômen e os glúteos sem deixar o quadril cair.",
        reps = 1,
        restSeconds = 45,
        weightKg = null,
        notes = "Segurar 40 segundos",
        totalSets = 3
    )
)

class ExerciseDetailActivity : AppCompatActivity() {

    companion object {
        const val EXERCISE_ID_KEY = "exercise_id"
    }
    private lateinit var binding: ExerciseDetailLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExerciseDetailLayoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val exerciseId = intent.getStringExtra(EXERCISE_ID_KEY)
        val exercise = mockExercises.find { it.id == exerciseId }
        if (exercise == null) {
            Toast.makeText(this, R.string.exercise_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val weightText = exercise.weightKg?.let { getString(R.string.weight_kg, it) }
            ?: getString(R.string.bodyweight)

        binding.name.text = exercise.name
        binding.description.text = exercise.description

        setDetailRow(binding.muscleGroupRow, R.string.muscle_group, exercise.muscleGroup)
        setDetailRow(binding.equipmentRow, R.string.equipment, exercise.equipment)
        setDetailRow(binding.repsRow, R.string.reps, exercise.reps.toString())
        setDetailRow(binding.weightRow, R.string.weight, weightText)
        setDetailRow(binding.restRow, R.string.rest, getString(R.string.rest_seconds, exercise.restSeconds))
        setDetailRow(binding.notesRow, R.string.notes, exercise.notes ?: getString(R.string.no_notes))

        createAndLayoutBadges(
            listOfNotNull(
                exercise.muscleGroup,
                exercise.equipment,
                getString(R.string.sets_x_reps, exercise.totalSets, exercise.reps),
                exercise.weightKg?.let { getString(R.string.weight_kg, it) }
            )
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.setsProgressContainer, SetsProgressFragment.newInstance(exercise.id))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setDetailRow(row: DetailRowLayoutBinding, labelId: Int, value: String) {
        row.label.setText(labelId)
        row.value.text = value
    }

    private fun createAndLayoutBadges(badgesTexts: List<String>) {
        val maxBadgesPerRow = 3

        badgesTexts.take(maxBadgesPerRow).forEach {
            createBadgeView(binding.badgeRow1, it)
        }

        badgesTexts.drop(maxBadgesPerRow).take(maxBadgesPerRow).forEach { badgeText ->
            createBadgeView(binding.badgeRow2, badgeText)
        }
    }

    private fun createBadgeView(row: LinearLayout, text: String) {
        val badgeLayoutBinding = BadgeLayoutBinding.inflate(layoutInflater, row, false)
        badgeLayoutBinding.badgeText.text = text
        row.addView(badgeLayoutBinding.root)
    }
}

data class ExerciseDetail(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val reps: Int,
    val restSeconds: Int,
    val weightKg: Double?,
    val notes: String?,
    val completedSets: Int = 0,
    val totalSets: Int
) {
    init {
        require(totalSets > 0) { "totalSets is not valid" }
        require(completedSets in 0..totalSets) { "completedSets is not valid" }
    }

    val progressPercentage: Int
        get() = ((completedSets.toFloat() / totalSets) * 100).toInt()

    fun completeSet(): ExerciseDetail? {
        return if (completedSets < totalSets) {
            copy(completedSets = completedSets + 1)
        } else {
            null
        }
    }

    fun undoSet(): ExerciseDetail? {
        return if (completedSets > 0) {
            copy(completedSets = completedSets - 1)
        } else {
            null
        }
    }
}
