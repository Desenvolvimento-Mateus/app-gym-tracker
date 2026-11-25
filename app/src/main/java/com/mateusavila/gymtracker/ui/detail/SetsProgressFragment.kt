package com.mateusavila.gymtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.data.ExerciseMockDataSource
import com.mateusavila.gymtracker.databinding.FragmentSetsProgressBinding
import com.mateusavila.gymtracker.databinding.ItemSetBinding
import com.mateusavila.gymtracker.domain.model.SetsProgress

class SetsProgressFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE_ID = "exercise_id"
        private const val STATE_COMPLETED_SETS = "completed_sets"

        fun newInstance(exerciseId: String) = SetsProgressFragment().apply {
            arguments = bundleOf(ARG_EXERCISE_ID to exerciseId)
        }
    }

    private var _binding: FragmentSetsProgressBinding? = null
    private val binding get() = _binding!!

    private val setBindings = mutableListOf<ItemSetBinding>()
    private lateinit var setsProgress: SetsProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exerciseId = requireArguments().getString(ARG_EXERCISE_ID).orEmpty()
        val exercise = checkNotNull(ExerciseMockDataSource.findById(exerciseId)) {
            "Exercise $exerciseId not found"
        }
        setsProgress = SetsProgress(
            totalSets = exercise.totalSets,
            completedSets = savedInstanceState?.getInt(STATE_COMPLETED_SETS) ?: 0
        )
    }

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

        createSetViews()
        binding.undoSetButton.setOnClickListener { updateProgress(setsProgress.undoSet()) }
        binding.completeSetButton.setOnClickListener { updateProgress(setsProgress.completeSet()) }

        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_COMPLETED_SETS, setsProgress.completedSets)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setBindings.clear()
        _binding = null
    }

    /** Inflates one reusable item_set.xml for each set of the exercise. */
    private fun createSetViews() {
        for (setNumber in 1..setsProgress.totalSets) {
            val setBinding = ItemSetBinding.inflate(layoutInflater, binding.setList, false)
            setBinding.root.text = setNumber.toString()
            setBinding.root.contentDescription = getString(R.string.set_description, setNumber)
            setBinding.root.setOnClickListener { updateProgress(setsProgress.toggleSet(setNumber)) }
            binding.setList.addView(setBinding.root)
            setBindings.add(setBinding)
        }
    }

    private fun updateProgress(newProgress: SetsProgress) {
        val wasFinished = setsProgress.isFinished
        setsProgress = newProgress
        render()

        if (!wasFinished && setsProgress.isFinished) {
            Toast.makeText(requireContext(), R.string.exercise_completed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun render() {
        binding.setsCounter.text =
            getString(R.string.sets_counter, setsProgress.completedSets, setsProgress.totalSets)
        binding.setsProgressPercent.text =
            getString(R.string.sets_progress_percent, setsProgress.progressPercentage)

        setBindings.forEachIndexed { index, setBinding ->
            setBinding.root.isActivated = index < setsProgress.completedSets
        }

        binding.undoSetButton.isEnabled = setsProgress.completedSets > 0
        binding.completeSetButton.isEnabled = !setsProgress.isFinished
        binding.completeSetButton.text = if (setsProgress.isFinished) {
            getString(R.string.all_sets_completed)
        } else {
            getString(R.string.complete_set_button, setsProgress.completedSets + 1)
        }
    }
}
