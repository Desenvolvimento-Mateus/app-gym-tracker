package com.mateusavila.gymtracker

import android.app.Application

/** Creates the AppContainer once, when the app process starts. */
class GymTrackerApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
