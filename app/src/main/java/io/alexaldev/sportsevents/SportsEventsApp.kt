package io.alexaldev.sportsevents

import android.app.Application
import io.alexaldev.sportsevents.data.dataModule
import io.alexaldev.sportsevents.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SportsEventsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SportsEventsApp)
            modules(presentationModule, dataModule)
        }
    }
}