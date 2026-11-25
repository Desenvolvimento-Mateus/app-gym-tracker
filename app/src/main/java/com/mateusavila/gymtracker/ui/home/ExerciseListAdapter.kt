package com.mateusavila.gymtracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.databinding.ItemExerciseBinding
import com.mateusavila.gymtracker.ui.weightText

class ExerciseListAdapter(
    private val items: List<ExerciseListItem>,
    private val onItemClick: (exerciseId: String) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
        val setsAndReps = context.getString(R.string.sets_x_reps, item.totalSets, item.reps)

        holder.binding.orderNumber.text = context.getString(R.string.order_number, item.order)
        holder.binding.exerciseName.text = item.name
        holder.binding.exerciseSummary.text =
            context.getString(R.string.exercise_summary, setsAndReps, context.weightText(item.weightInGrams))
        holder.binding.muscleGroupChip.root.text = item.muscleGroup
        holder.binding.root.setOnClickListener { onItemClick(item.id) }
    }

    override fun getItemCount(): Int = items.size
}
