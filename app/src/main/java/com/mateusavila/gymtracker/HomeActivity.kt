package com.mateusavila.gymtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateusavila.gymtracker.databinding.ActivityHomeBinding
import com.mateusavila.gymtracker.databinding.HomeListItemLayoutBinding

val homeList = mockExercises.map {
    ExerciseListItem(it.id, it.name, it.muscleGroup, it.totalSets)
}

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listView.adapter = ExerciseListAdapter(homeList)
        binding.listView.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

class ExerciseListAdapter(val list: List<ExerciseListItem>) :
    RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {
    class ViewHolder(val listItemBinding: HomeListItemLayoutBinding) :
        RecyclerView.ViewHolder(listItemBinding.root) {
        val context: Context = listItemBinding.root.context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val listItemBinding =
            HomeListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = list[position]

        holder.listItemBinding.name.text = item.name
        holder.listItemBinding.setsCount.text =
            holder.context.getString(R.string.home_list_item_sets, item.totalSets)
        holder.listItemBinding.badge.badgeText.text = item.muscleGroup

        holder.listItemBinding.root.setOnClickListener {
            val intent = Intent(holder.context, ExerciseDetailActivity::class.java)
            intent.putExtra(ExerciseDetailActivity.EXERCISE_ID_KEY, item.id)
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

data class ExerciseListItem(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val totalSets: Int
)
