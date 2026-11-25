package com.mateusavila.gymtracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// The app is always dark: it is part of its visual identity.
private val GymColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    primaryContainer = AccentContainer,
    onPrimaryContainer = Accent,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = ErrorColor,
    onError = OnAccent
)

/** Rounded corners used by every card of the app. */
val CardShape = RoundedCornerShape(20.dp)

@Composable
fun GymTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GymColorScheme,
        typography = GymTypography,
        content = content
    )
}
