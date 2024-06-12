package com.github.aivanovski.picoautomator.android.di

import com.github.aivanovski.picoautomator.android.data.repository.FlowRepository
import com.github.aivanovski.picoautomator.android.data.repository.FlowRepositoryImpl
import com.github.aivanovski.picoautomator.android.data.Settings
import com.github.aivanovski.picoautomator.android.data.SettingsImpl
import com.github.aivanovski.picoautomator.android.data.api.ApiClient
import com.github.aivanovski.picoautomator.android.data.db.AppDatabase
import com.github.aivanovski.picoautomator.android.data.db.dao.ExecutionDataDao
import com.github.aivanovski.picoautomator.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.picoautomator.android.data.db.dao.JobDao
import com.github.aivanovski.picoautomator.android.data.db.dao.StepEntryDao
import com.github.aivanovski.picoautomator.android.data.repository.ExecutionDataRepository
import com.github.aivanovski.picoautomator.android.data.repository.JobRepository
import com.github.aivanovski.picoautomator.android.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.picoautomator.android.domain.usecases.ParseFlowFileUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.dsl.module
import timber.log.Timber

object AndroidAppModule {

    val module = module {
        single<Settings> { SettingsImpl(get()) }

        // Database
        single { AppDatabase.buildDatabase(get()) }
        single { provideStepEntryDao(get()) }
        single { provideFlowEntryDao(get()) }
        single { provideRunnerEntryDao(get()) }
        single { provideExecutionEntryDao(get()) }

        // Network
        single { provideHttpClient() }
        single { ApiClient(get(), get()) }

        // Repositories
        single<FlowRepository> { FlowRepositoryImpl(get(), get(), get(), get()) }
        single { JobRepository(get()) }
        single { ExecutionDataRepository(get()) }

        // UseCases
        single { ParseFlowFileUseCase() }
        single { GetCurrentJobUseCase(get()) }

        // Interactors
        single { FlowInteractor(get(), get(), get(), get(), get(), get()) }
    }

    private fun provideStepEntryDao(db: AppDatabase): StepEntryDao = db.stepEntryDao

    private fun provideFlowEntryDao(db: AppDatabase): FlowEntryDao = db.flowEntryDao

    private fun provideRunnerEntryDao(db: AppDatabase): JobDao = db.runnerEntryDao

    private fun provideExecutionEntryDao(db: AppDatabase): ExecutionDataDao =
        db.executionDataDao

    private fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.BODY
            }
        }
    }
}