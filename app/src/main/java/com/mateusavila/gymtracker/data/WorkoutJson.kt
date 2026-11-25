package com.mateusavila.gymtracker.data

import kotlinx.serialization.json.Json

/**
 * Json configuration shared by the data sources.
 * Unknown keys are ignored so an extra field in the file does not break the app.
 */
val workoutJson = Json { ignoreUnknownKeys = true }
