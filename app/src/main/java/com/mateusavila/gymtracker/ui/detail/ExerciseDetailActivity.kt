package com.mateusavila.gymtracker.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.ExerciseMockDataSource
import com.mateusavila.gymtracker.databinding.ActivityExerciseDetailBinding
import com.mateusavila.gymtracker.domain.model.Exercise
import com.mateusavila.gymtracker.ui.applySystemBarsPadding
import com.mateusavila.gymtracker.ui.bind
import com.mateusavila.gymtracker.ui.formatWeightInKg

class ExerciseDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_EXERCISE_ID = "exercise_id"

        /** Only the exercise id goes through the Intent; the screen looks up the rest. */
        fun createIntent(context: Context, exerciseId: String): Intent =
            Intent(context, ExerciseDetailActivity::class.java)
                .putExtra(EXTRA_EXERCISE_ID, exerciseId)
    }

    private lateinit var binding: ActivityExerciseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExerciseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.detailRoot.applySystemBarsPadding()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val exercise = intent.getStringExtra(EXTRA_EXERCISE_ID)?.let(ExerciseMockDataSource::findById)
        if (exercise == null) {
            Toast.makeText(this, R.string.exercise_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showExercise(exercise)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.sets_progress_container, SetsProgressFragment.newInstance(exercise.id))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showExercise(exercise: Exercise) {
        binding.muscleGroupLabel.text =
            getString(R.string.muscle_group_and_equipment, exercise.muscleGroup, exercise.equipment)
        binding.exerciseName.text = exercise.name
        binding.exerciseDescription.text = exercise.description

        val weightValue = exercise.weightInGrams
            ?.let { getString(R.string.weight_in_kg, formatWeightInKg(it)) }
            ?: getString(R.string.bodyweight_short)

        binding.setsTile.bind(exercise.totalSets.toString(), R.string.sets_label)
        binding.repsTile.bind(exercise.reps.toString(), R.string.stat_reps)
        binding.weightTile.bind(weightValue, R.string.stat_weight)
        binding.restTile.bind(getString(R.string.rest_seconds, exercise.restSeconds), R.string.stat_rest)

        // Optional notes: the card is only shown when the exercise has a tip.
        binding.notesCard.isVisible = exercise.notes != null
        binding.notesText.text = exercise.notes
    }
}
