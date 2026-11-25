package com.mateusavila.gymtracker.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusavila.gymtracker.R
import com.mateusavila.gymtracker.ui.components.BackTopBar
import com.mateusavila.gymtracker.ui.components.EmptyState
import com.mateusavila.gymtracker.ui.components.ErrorState
import com.mateusavila.gymtracker.ui.components.LoadingState
import com.mateusavila.gymtracker.ui.components.Overline
import com.mateusavila.gymtracker.ui.components.ProgressBar
import com.mateusavila.gymtracker.ui.components.StatTile
import com.mateusavila.gymtracker.ui.formatWeightInKg
import com.mateusavila.gymtracker.ui.theme.CardShape
import com.mateusavila.gymtracker.ui.theme.GymTrackerTheme

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadHistory
    )
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        BackTopBar(onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Overline(text = stringResource(R.string.history_subtitle), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        when (uiState) {
            HistoryUiState.Loading -> LoadingState(message = stringResource(R.string.loading_history))

            is HistoryUiState.Error -> ErrorState(
                message = stringResource(uiState.messageRes),
                onRetry = onRetry
            )

            HistoryUiState.Empty -> EmptyState(
                title = stringResource(R.string.empty_history_title),
                message = stringResource(R.string.empty_history_message)
            )

            is HistoryUiState.Success -> SessionList(uiState)
        }
    }
}

@Composable
private fun SessionList(state: HistoryUiState.Success) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                StatTile(
                    value = state.sessions.size.toString(),
                    label = stringResource(R.string.history_workouts),
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    value = state.totalCompletedSets.toString(),
                    label = stringResource(R.string.history_completed_sets),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        items(items = state.sessions, key = { it.id }) { session ->
            SessionRow(session)
        }
    }
}

@Composable
private fun SessionRow(session: WorkoutSessionItem) {
    val colors = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, CardShape)
            .padding(16.dp)
    ) {
        // Date block on the left side of the card.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(colors.primaryContainer, CardShape)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = session.time,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
            Text(
                text = session.date,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
        }

        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.workoutName,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.history_sets_done, session.completedSets, session.totalSets),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.history_total_load,
                    stringResource(R.string.weight_in_kg, formatWeightInKg(session.totalLoadInGrams))
                ),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            ProgressBar(percentage = session.progressPercentage)
        }
    }
}

@Preview
@Composable
private fun HistoryContentPreview() {
    GymTrackerTheme {
        HistoryContent(
            uiState = HistoryUiState.Success(
                sessions = listOf(
                    WorkoutSessionItem("1", "Treino A · Corpo inteiro", "25/11/2026", "18:40", 20, 28, 71, 4_520_000),
                    WorkoutSessionItem("2", "Treino A · Corpo inteiro", "23/11/2026", "07:15", 28, 28, 100, 6_100_000)
                ),
                totalCompletedSets = 48
            ),
            onBack = {},
            onRetry = {}
        )
    }
}
