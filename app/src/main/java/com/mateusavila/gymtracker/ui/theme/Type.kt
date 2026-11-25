package com.mateusavila.gymtracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mateusavila.gymtracker.R

// Both font files are variable fonts (one file with every weight). Compose sets the
// font "weight" axis from the FontWeight given here, so the same file is used three times.

/** Syne: titles and body text. */
val Syne = FontFamily(
    Font(R.font.syne, FontWeight.Normal),
    Font(R.font.syne, FontWeight.SemiBold),
    Font(R.font.syne, FontWeight.Bold)
)

/** JetBrains Mono: numbers (sets, weight, percentages) and small labels. */
val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Bold)
)

/**
 * How the app uses each Material slot:
 * - headlineLarge/Medium: screen titles
 * - headlineSmall: big numbers of the stat tiles (mono)
 * - titleMedium: exercise names in the lists
 * - labelMedium: small mono texts, such as "4 × 10 · 40 kg"
 * - labelSmall: uppercase overline above titles and sections (mono)
 */
val GymTypography = Typography(
    headlineLarge = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = Syne, fontSize = 15.sp, lineHeight = 21.sp),
    bodyMedium = TextStyle(fontFamily = Syne, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Syne, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Syne, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = JetBrainsMono, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.12.em
    )
)
