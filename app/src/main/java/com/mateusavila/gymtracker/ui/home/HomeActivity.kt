package com.mateusavila.gymtracker.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.ExerciseMockDataSource
import com.mateusavila.gymtracker.databinding.ActivityHomeBinding
import com.mateusavila.gymtracker.ui.applySystemBarsPadding
import com.mateusavila.gymtracker.ui.bind
import com.mateusavila.gymtracker.ui.detail.ExerciseDetailActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.homeRoot.applySystemBarsPadding()

        val exercises = ExerciseMockDataSource.getExercises()

        showSummary(exercises.toWorkoutSummary())

        binding.exerciseList.layoutManager = LinearLayoutManager(this)
        binding.exerciseList.adapter = ExerciseListAdapter(
            items = exercises.mapIndexed { index, exercise -> exercise.toListItem(order = index + 1) },
            onItemClick = ::openExerciseDetail
        )
    }

    private fun showSummary(summary: WorkoutSummary) {
        binding.exercisesTile.bind(summary.exerciseCount.toString(), R.string.summary_exercises)
        binding.setsTile.bind(summary.totalSets.toString(), R.string.sets_label)
        binding.muscleGroupsTile.bind(summary.muscleGroupCount.toString(), R.string.summary_muscle_groups)
    }

    private fun openExerciseDetail(exerciseId: String) {
        startActivity(ExerciseDetailActivity.createIntent(this, exerciseId))
    }
}
