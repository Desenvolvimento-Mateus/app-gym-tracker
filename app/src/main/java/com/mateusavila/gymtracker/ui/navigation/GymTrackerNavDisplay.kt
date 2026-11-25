package com.mateusavila.gymtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mateusavila.gymtracker.AppContainer
import com.mateusavila.gymtracker.ui.detail.ExerciseDetailScreen
import com.mateusavila.gymtracker.ui.detail.ExerciseDetailViewModel
import com.mateusavila.gymtracker.ui.history.HistoryScreen
import com.mateusavila.gymtracker.ui.history.HistoryViewModel
import com.mateusavila.gymtracker.ui.home.HomeScreen
import com.mateusavila.gymtracker.ui.home.HomeViewModel

/**
 * Navigation 3: the back stack is a list of routes that we change ourselves
 * (add to open a screen, remove the last one to go back). NavDisplay shows the last route.
 */
@Composable
fun GymTrackerNavDisplay(container: AppContainer) {
    val backStack = rememberNavBackStack(HomeRoute)
    val repository = container.workoutRepository

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.goBack() },
        entryDecorators = listOf(
            // Keeps the rememberSaveable state of each screen.
            rememberSaveableStateHolderNavEntryDecorator(),
            // Gives each screen its own ViewModels, cleared when the screen leaves the back stack.
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    viewModel = viewModel { HomeViewModel(repository) },
                    onExerciseClick = { exerciseId -> backStack.open(ExerciseDetailRoute(exerciseId)) },
                    onHistoryClick = { backStack.open(HistoryRoute) },
                    onWorkoutFinished = { backStack.open(HistoryRoute) }
                )
            }
            entry<ExerciseDetailRoute> { route ->
                ExerciseDetailScreen(
                    viewModel = viewModel { ExerciseDetailViewModel(route.exerciseId, repository) },
                    onBack = { backStack.goBack() }
                )
            }
            entry<HistoryRoute> {
                HistoryScreen(
                    viewModel = viewModel { HistoryViewModel(repository) },
                    onBack = { backStack.goBack() }
                )
            }
        }
    )
}

/** Ignores a second tap that would open the same screen twice. */
private fun NavBackStack<NavKey>.open(route: NavKey) {
    if (lastOrNull() != route) add(route)
}

/** The home screen is never removed: the back stack cannot be empty. */
private fun NavBackStack<NavKey>.goBack() {
    if (size > 1) removeAt(lastIndex)
}
