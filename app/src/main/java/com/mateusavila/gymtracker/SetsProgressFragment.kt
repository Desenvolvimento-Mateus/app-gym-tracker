package com.mateusavila.gymtracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mateusavila.gymtracker.databinding.FragmentSetsProgressBinding

class SetsProgressFragment : Fragment() {

    companion object {
        private const val EXERCISE_ID_KEY = "exercise_id"
        private const val COMPLETED_SETS_KEY = "completed_sets"

        fun newInstance(exerciseId: String): SetsProgressFragment {
            val arguments = Bundle()
            arguments.putString(EXERCISE_ID_KEY, exerciseId)

            val fragment = SetsProgressFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private var _binding: FragmentSetsProgressBinding? = null
    private val binding get() = _binding!!
    private lateinit var exercise: ExerciseDetail

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetsProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exerciseId = requireArguments().getString(EXERCISE_ID_KEY)
        val completedSets = savedInstanceState?.getInt(COMPLETED_SETS_KEY) ?: 0
        exercise = mockExercises.first { it.id == exerciseId }.copy(completedSets = completedSets)

        binding.fabUndoSet.setOnClickListener {
            exercise.undoSet()?.let { exercise = it }
            updateSetsProgressViews()
        }
        binding.fabCompleteSet.setOnClickListener {
            exercise.completeSet()?.let { exercise = it }
            updateSetsProgressViews()
            if (exercise.completedSets == exercise.totalSets) {
                Toast.makeText(requireContext(), R.string.exercise_completed, Toast.LENGTH_SHORT).show()
            }
        }

        updateSetsProgressViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::exercise.isInitialized) {
            outState.putInt(COMPLETED_SETS_KEY, exercise.completedSets)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateSetsProgressViews() {
        binding.setsProgressText.text =
            getString(R.string.sets_progress, exercise.completedSets, exercise.totalSets)
        binding.setsProgressPercent.text = getString(R.string.percent, exercise.progressPercentage)
        binding.setsProgressIndicator.progress = exercise.progressPercentage
        binding.fabUndoSet.isEnabled = exercise.completedSets > 0
        binding.fabCompleteSet.isEnabled = exercise.completedSets < exercise.totalSets
    }
}
