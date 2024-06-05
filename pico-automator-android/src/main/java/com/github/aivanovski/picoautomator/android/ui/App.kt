package com.github.aivanovski.picoautomator.android.ui

import androidx.multidex.MultiDexApplication
import com.github.aivanovski.picoautomator.android.ui.di.KoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(KoinModule.appModule)
        }
    }
}