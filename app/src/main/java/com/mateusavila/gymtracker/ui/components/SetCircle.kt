package com.mateusavila.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mateusavila.gymtracker.R

/**
 * One set of the exercise as a numbered circle: filled with the accent color when done,
 * only outlined when pending. Tapping it toggles the set.
 */
@Composable
fun SetCircle(
    setNumber: Int,
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val setDescription = stringResource(R.string.set_description, setNumber)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .then(
                if (isDone) {
                    Modifier.background(colors.primary)
                } else {
                    Modifier.border(2.dp, colors.outline, CircleShape)
                }
            )
            // toggleable (instead of clickable) tells TalkBack that the set is checked or not.
            .toggleable(value = isDone, onValueChange = { onClick() })
            .semantics { contentDescription = setDescription }
    ) {
        Text(
            text = setNumber.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (isDone) colors.onPrimary else colors.onSurfaceVariant
        )
    }
}
