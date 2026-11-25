package com.mateusavila.gymtracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mateusavila.gymtracker.ui.navigation.GymTrackerNavDisplay
import com.mateusavila.gymtracker.ui.theme.GymTrackerTheme

/** The only Activity of the app. Every screen is a composable shown by Navigation 3. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app is always dark, so the system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val container = (application as GymTrackerApplication).container
        setContent {
            GymTrackerTheme {
                GymTrackerNavDisplay(container)
            }
        }
    }
}
