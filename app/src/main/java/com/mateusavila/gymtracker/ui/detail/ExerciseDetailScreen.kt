package com.mateusavila.gymtracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.domain.model.ExerciseProgress
import com.mateusavila.gymtracker.ui.components.BackTopBar
import com.mateusavila.gymtracker.ui.components.ErrorState
import com.mateusavila.gymtracker.ui.components.LoadingState
import com.mateusavila.gymtracker.ui.components.Overline
import com.mateusavila.gymtracker.ui.components.SetCircle
import com.mateusavila.gymtracker.ui.components.StatTile
import com.mateusavila.gymtracker.ui.formatWeightInKg
import com.mateusavila.gymtracker.ui.theme.CardShape
import com.mateusavila.gymtracker.ui.theme.GymTrackerTheme

@Composable
fun ExerciseDetailScreen(
    viewModel: ExerciseDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseDetailContent(
        uiState = uiState,
        actions = ExerciseDetailActions(
            onBack = onBack,
            onRetry = viewModel::loadExercise,
            onSetClick = viewModel::onSetClick,
            onCompleteSet = viewModel::onCompleteSet,
            onUndoSet = viewModel::onUndoSet,
            onIncreaseWeight = viewModel::onIncreaseWeight,
            onDecreaseWeight = viewModel::onDecreaseWeight
        )
    )
}

/** Groups the callbacks of the screen so the composables do not need seven parameters. */
data class ExerciseDetailActions(
    val onBack: () -> Unit,
    val onRetry: () -> Unit,
    val onSetClick: (setNumber: Int) -> Unit,
    val onCompleteSet: () -> Unit,
    val onUndoSet: () -> Unit,
    val onIncreaseWeight: () -> Unit,
    val onDecreaseWeight: () -> Unit
)

@Composable
fun ExerciseDetailContent(
    uiState: ExerciseDetailUiState,
    actions: ExerciseDetailActions
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = actions.onBack)

        when (uiState) {
            ExerciseDetailUiState.Loading -> LoadingState(message = stringResource(R.string.loading_exercise))

            is ExerciseDetailUiState.Error -> ErrorState(
                message = stringResource(uiState.messageRes),
                onRetry = if (uiState.canRetry) actions.onRetry else null
            )

            is ExerciseDetailUiState.Success -> ExerciseDetails(
                exercise = uiState.exercise,
                actions = actions
            )
        }
    }
}

@Composable
private fun ExerciseDetails(
    exercise: ExerciseDetailUi,
    actions: ExerciseDetailActions
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Overline(
            text = stringResource(R.string.muscle_group_and_equipment, exercise.muscleGroup, exercise.equipment),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))
        StatGrid(exercise)

        // Optional weight: bodyweight exercises (null) have nothing to adjust.
        if (exercise.weightInGrams != null) {
            Spacer(Modifier.height(8.dp))
            WeightAdjuster(
                weightInGrams = exercise.weightInGrams,
                canDecrease = exercise.canDecreaseWeight,
                onIncrease = actions.onIncreaseWeight,
                onDecrease = actions.onDecreaseWeight
            )
        }

        Spacer(Modifier.height(16.dp))
        SetsCard(exercise = exercise, actions = actions)

        Spacer(Modifier.height(24.dp))
        Overline(text = stringResource(R.string.execution_title))
        Spacer(Modifier.height(8.dp))
        Text(
            text = exercise.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Optional notes: the tip card only exists when the exercise has a tip.
        exercise.notes?.let { notes ->
            Spacer(Modifier.height(16.dp))
            NotesCard(notes)
        }
    }
}

/** 2 x 2 grid of stat tiles. */
@Composable
private fun StatGrid(exercise: ExerciseDetailUi) {
    val weightValue = exercise.weightInGrams
        ?.let { stringResource(R.string.weight_in_kg, formatWeightInKg(it)) }
        ?: stringResource(R.string.bodyweight_short)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(
                value = exercise.totalSets.toString(),
                label = stringResource(R.string.sets_label),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = exercise.reps.toString(),
                label = stringResource(R.string.stat_reps),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(
                value = weightValue,
                label = stringResource(R.string.stat_weight),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = stringResource(R.string.rest_seconds, exercise.restSeconds),
                label = stringResource(R.string.stat_rest),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeightAdjuster(
    weightInGrams: Int,
    canDecrease: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val buttonColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Overline(text = stringResource(R.string.adjust_weight_title))
            Text(
                text = stringResource(
                    R.string.weight_step_hint,
                    formatWeightInKg(ExerciseProgress.WEIGHT_STEP_IN_GRAMS)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FilledTonalIconButton(onClick = onDecrease, enabled = canDecrease, colors = buttonColors) {
            Icon(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = stringResource(R.string.decrease_weight)
            )
        }
        Text(
            text = stringResource(R.string.weight_in_kg, formatWeightInKg(weightInGrams)),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        FilledTonalIconButton(onClick = onIncrease, colors = buttonColors) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.increase_weight)
            )
        }
    }
}

@Composable
private fun SetsCard(
    exercise: ExerciseDetailUi,
    actions: ExerciseDetailActions
) {
    val colors = MaterialTheme.colorScheme
    // The card gets the accent border when every set is done.
    val cardModifier = if (exercise.isFinished) {
        Modifier.border(1.dp, colors.primary, CardShape)
    } else {
        Modifier
    }

    Column(
        modifier = cardModifier
            .fillMaxWidth()
            .background(colors.surface, CardShape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.sets_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.progress_percent, exercise.progressPercentage),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = if (exercise.isFinished) {
                stringResource(R.string.exercise_completed)
            } else {
                stringResource(R.string.sets_counter, exercise.completedSets, exercise.totalSets)
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (exercise.isFinished) colors.primary else colors.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (setNumber in 1..exercise.totalSets) {
                SetCircle(
                    setNumber = setNumber,
                    isDone = setNumber <= exercise.completedSets,
                    onClick = { actions.onSetClick(setNumber) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row {
            OutlinedButton(
                onClick = actions.onUndoSet,
                enabled = exercise.completedSets > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.undo_set_button))
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = actions.onCompleteSet,
                enabled = !exercise.isFinished,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    if (exercise.isFinished) {
                        stringResource(R.string.all_sets_completed)
                    } else {
                        stringResource(R.string.complete_set_button, exercise.completedSets + 1)
                    }
                )
            }
        }
    }
}

@Composable
private fun NotesCard(notes: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, CardShape)
            .border(1.dp, MaterialTheme.colorScheme.primary, CardShape)
            .padding(16.dp)
    ) {
        Overline(text = stringResource(R.string.notes_title), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(
            text = notes,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun ExerciseDetailContentPreview() {
    GymTrackerTheme {
        ExerciseDetailContent(
            uiState = ExerciseDetailUiState.Success(
                ExerciseDetailUi(
                    name = "Supino reto",
                    muscleGroup = "Peito",
                    equipment = "Barra",
                    description = "Deitado no banco, desça a barra até o peito e empurre de volta.",
                    reps = 10,
                    restSeconds = 90,
                    notes = "Escápulas retraídas",
                    totalSets = 4,
                    completedSets = 2,
                    progressPercentage = 50,
                    isFinished = false,
                    weightInGrams = 42_500,
                    canDecreaseWeight = true
                )
            ),
            actions = ExerciseDetailActions({}, {}, {}, {}, {}, {}, {})
        )
    }
}
