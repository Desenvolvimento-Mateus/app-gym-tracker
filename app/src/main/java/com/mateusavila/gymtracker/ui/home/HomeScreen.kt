package com.mateusavila.gymtracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.ui.components.EmptyState
import com.mateusavila.gymtracker.ui.components.ErrorState
import com.mateusavila.gymtracker.ui.components.LoadingState
import com.mateusavila.gymtracker.ui.components.Overline
import com.mateusavila.gymtracker.ui.components.ProgressBar
import com.mateusavila.gymtracker.ui.components.StatTile
import com.mateusavila.gymtracker.ui.components.TagChip
import com.mateusavila.gymtracker.ui.theme.CardShape
import com.mateusavila.gymtracker.ui.theme.GymTrackerTheme
import com.mateusavila.gymtracker.ui.weightText

/**
 * Connects the HomeViewModel to the screen: collects the state and the events
 * and turns the user actions into ViewModel calls.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onExerciseClick: (exerciseId: String) -> Unit,
    onHistoryClick: () -> Unit,
    onWorkoutFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val finishErrorMessage = stringResource(R.string.finish_workout_error)
    val currentOnWorkoutFinished by rememberUpdatedState(onWorkoutFinished)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                HomeEvent.WorkoutFinished -> currentOnWorkoutFinished()
                HomeEvent.FinishFailed -> snackbarHostState.showSnackbar(finishErrorMessage)
            }
        }
    }

    HomeContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = viewModel::loadWorkout,
        onExerciseClick = onExerciseClick,
        onHistoryClick = onHistoryClick,
        onFinishWorkout = viewModel::finishWorkout
    )
}

/** Stateless screen: only shows [uiState] and reports the clicks. */
@Composable
fun HomeContent(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onExerciseClick: (exerciseId: String) -> Unit,
    onHistoryClick: () -> Unit,
    onFinishWorkout: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is HomeUiState.Success) {
                FinishWorkoutBar(
                    canFinish = uiState.canFinishWorkout,
                    isFinishing = uiState.isFinishing,
                    onFinishWorkout = onFinishWorkout
                )
            }
        }
    ) { innerPadding ->
        val stateModifier = Modifier.padding(innerPadding)
        when (uiState) {
            HomeUiState.Loading -> LoadingState(
                message = stringResource(R.string.loading_workout),
                modifier = stateModifier
            )

            is HomeUiState.Error -> ErrorState(
                message = stringResource(uiState.messageRes),
                modifier = stateModifier,
                onRetry = onRetry
            )

            HomeUiState.Empty -> EmptyState(
                title = stringResource(R.string.empty_workout_title),
                message = stringResource(R.string.empty_workout_message),
                modifier = stateModifier
            )

            is HomeUiState.Success -> WorkoutList(
                state = uiState,
                contentPadding = innerPadding,
                onExerciseClick = onExerciseClick,
                onHistoryClick = onHistoryClick
            )
        }
    }
}

@Composable
private fun WorkoutList(
    state: HomeUiState.Success,
    contentPadding: PaddingValues,
    onExerciseClick: (exerciseId: String) -> Unit,
    onHistoryClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        )
    ) {
        item {
            WorkoutHeader(
                workoutName = state.workoutName,
                summary = state.summary,
                onHistoryClick = onHistoryClick
            )
        }
        item {
            Overline(
                text = stringResource(R.string.exercise_list_title),
                modifier = Modifier.padding(top = 12.dp, start = 4.dp)
            )
        }
        // The key keeps each row tied to its exercise when the list changes.
        items(items = state.exercises, key = { it.id }) { exercise ->
            ExerciseRow(
                item = exercise,
                onClick = { onExerciseClick(exercise.id) }
            )
        }
    }
}

@Composable
private fun WorkoutHeader(
    workoutName: String,
    summary: WorkoutSummary,
    onHistoryClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_dumbbell),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Overline(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            HistoryButton(onClick = onHistoryClick)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = workoutName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(
                value = summary.exerciseCount.toString(),
                label = stringResource(R.string.summary_exercises),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = stringResource(R.string.sets_done_of_total, summary.completedSets, summary.totalSets),
                label = stringResource(R.string.sets_label),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = summary.muscleGroupCount.toString(),
                label = stringResource(R.string.summary_muscle_groups),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Overline(
                text = stringResource(R.string.workout_progress_title),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.progress_percent, summary.progressPercentage),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        ProgressBar(percentage = summary.progressPercentage)
    }
}

/** Pill-shaped button that opens the history. */
@Composable
private fun HistoryButton(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
    ) {
        Overline(
            text = stringResource(R.string.history_button),
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ExerciseRow(
    item: ExerciseListItem,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val setsAndReps = stringResource(R.string.sets_x_reps, item.totalSets, item.reps)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Order number of the exercise; filled with the accent color when every set is done.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .background(if (item.isFinished) colors.primary else colors.primaryContainer, CircleShape)
        ) {
            Text(
                text = stringResource(R.string.order_number, item.order),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (item.isFinished) colors.onPrimary else colors.primary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.exercise_summary, setsAndReps, weightText(item.weightInGrams)),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagChip(text = item.muscleGroup)
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.sets_done_of_total, item.completedSets, item.totalSets),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (item.isFinished) colors.primary else colors.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            ProgressBar(percentage = item.progressPercentage)
        }

        Icon(
            painter = painterResource(R.drawable.ic_chevron_forward),
            contentDescription = null,
            tint = colors.onSurfaceVariant
        )
    }
}

/** Fixed bar at the bottom with the "Finalizar treino" button. */
@Composable
private fun FinishWorkoutBar(
    canFinish: Boolean,
    isFinishing: Boolean,
    onFinishWorkout: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onFinishWorkout,
            enabled = canFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isFinishing) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.saving_workout))
            } else {
                Text(stringResource(R.string.finish_workout_button))
            }
        }
        if (!canFinish && !isFinishing) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.finish_workout_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    val exercises = listOf(
        ExerciseListItem("ex1", 1, "Supino reto", "Peito", 4, 10, 40_000, 4, 100, true),
        ExerciseListItem("ex2", 2, "Agachamento livre", "Pernas", 4, 8, 60_000, 1, 25, false),
        ExerciseListItem("ex8", 3, "Prancha", "Core", 3, 1, null, 0, 0, false)
    )
    GymTrackerTheme {
        HomeContent(
            uiState = HomeUiState.Success(
                workoutName = "Treino A · Corpo inteiro",
                summary = WorkoutSummary(3, 11, 5, 3, 45),
                exercises = exercises,
                canFinishWorkout = true,
                isFinishing = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRetry = {},
            onExerciseClick = {},
            onHistoryClick = {},
            onFinishWorkout = {}
        )
    }
}
