package com.mateusavila.gymtracker.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mateusavila.gymtracker.R

private const val GRAMS_PER_KILOGRAM = 1000

/**
 * Formats a weight in grams as kilograms using only integer math.
 * Examples: 40000 -> "40", 12500 -> "12,5", 2250 -> "2,25".
 */
fun formatWeightInKg(weightInGrams: Int): String {
    val kilograms = weightInGrams / GRAMS_PER_KILOGRAM
    val remainingGrams = weightInGrams % GRAMS_PER_KILOGRAM
    if (remainingGrams == 0) return kilograms.toString()

    val decimals = remainingGrams.toString().padStart(3, '0').trimEnd('0')
    return "$kilograms,$decimals"
}

/** "40 kg" when there is a weight, "Peso corporal" when it is null (bodyweight exercise). */
@Composable
fun weightText(weightInGrams: Int?): String =
    if (weightInGrams == null) {
        stringResource(R.string.bodyweight)
    } else {
        stringResource(R.string.weight_in_kg, formatWeightInKg(weightInGrams))
    }

/** "40 kg" when there is a weight, "Peso corporal" when it is null. */
fun Context.weightText(weightInGrams: Int?): String =
    weightInGrams?.let { getString(R.string.weight_in_kg, formatWeightInKg(it)) }
        ?: getString(R.string.bodyweight)
