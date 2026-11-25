package com.mateusavila.gymtracker.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Each screen is a key of the back stack. They are @Serializable so the back stack
// can be saved and restored (rotation, app going to the background).

@Serializable
data object HomeRoute : NavKey

/** Only the id goes to the detail screen; its ViewModel looks up the rest. */
@Serializable
data class ExerciseDetailRoute(val exerciseId: String) : NavKey

@Serializable
data object HistoryRoute : NavKey
