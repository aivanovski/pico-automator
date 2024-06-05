package com.github.aivanovski.picoautomator.android.ui.di

import com.github.aivanovski.picoautomator.android.ui.data.FlowRepository
import com.github.aivanovski.picoautomator.android.ui.data.FlowRepositoryImpl
import com.github.aivanovski.picoautomator.android.ui.data.Settings
import com.github.aivanovski.picoautomator.android.ui.data.SettingsImpl
import com.github.aivanovski.picoautomator.android.ui.data.api.FlowApi
import com.github.aivanovski.picoautomator.android.ui.data.api.MockedFlowApi
import com.github.aivanovski.picoautomator.android.ui.data.db.AppDatabase
import com.github.aivanovski.picoautomator.android.ui.data.db.dao.StepInfoDao
import com.github.aivanovski.picoautomator.android.ui.domain.FlowInteractor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Settings> { SettingsImpl(get()) }

        // Database
        single { AppDatabase.buildDatabase(get(), get()) }
        single { provideFlowStepDao(get()) }

        // Network
        single { provideMoshi() }
        single<FlowApi> { MockedFlowApi(get()) }

        // Repositories
        single<FlowRepository> { FlowRepositoryImpl(get(), get()) }

        // Interactors
        single { FlowInteractor(get(), get()) }
    }

    private fun provideFlowStepDao(db: AppDatabase): StepInfoDao = db.flowStepDao

    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}