package me.siddheshkothadi.autofism3

import android.app.Application
import timber.log.Timber

class FishApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}