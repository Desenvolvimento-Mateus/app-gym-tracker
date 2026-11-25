package com.mateusavila.gymtracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mateusavila.gymtracker.R

private const val GRAMS_PER_KILOGRAM = 1000

/**
 * Formats a weight in grams as kilograms using only integer math.
 * Examples: 40000 -> "40", 12500 -> "12,5", 2250 -> "2,25", 4520000 -> "4.520".
 */
fun formatWeightInKg(weightInGrams: Int): String {
    val kilograms = groupThousands(weightInGrams / GRAMS_PER_KILOGRAM)
    val remainingGrams = weightInGrams % GRAMS_PER_KILOGRAM
    if (remainingGrams == 0) return kilograms

    val decimals = remainingGrams.toString().padStart(3, '0').trimEnd('0')
    return "$kilograms,$decimals"
}

/** 4520 -> "4.520", the Brazilian thousands separator. */
private fun groupThousands(number: Int): String =
    number.toString().reversed().chunked(3).joinToString(".").reversed()

/** "40 kg" when there is a weight, "Peso corporal" when it is null (bodyweight exercise). */
@Composable
fun weightText(weightInGrams: Int?): String =
    if (weightInGrams == null) {
        stringResource(R.string.bodyweight)
    } else {
        stringResource(R.string.weight_in_kg, formatWeightInKg(weightInGrams))
    }
