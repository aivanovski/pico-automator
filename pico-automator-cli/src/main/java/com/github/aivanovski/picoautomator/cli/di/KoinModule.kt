package com.github.aivanovski.picoautomator.cli.di

import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProviderImpl
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProviderImpl
import com.github.aivanovski.picoautomator.cli.domain.MainInteractor
import com.github.aivanovski.picoautomator.cli.domain.argument.ArgumentParser
import com.github.aivanovski.picoautomator.cli.domain.usecases.GetVersionUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.RunTestUseCase
import com.github.aivanovski.picoautomator.presentation.OutputWriter
import com.github.aivanovski.picoautomator.presentation.StandardOutputWriter
import org.koin.dsl.module

object KoinModule {
    val appModule = module {
        single<FileProvider> { FileProviderImpl() }
        single<ResourceProvider> { ResourceProviderImpl() }
        single { ArgumentParser(get()) }
        single<OutputWriter> { StandardOutputWriter() }

        // use cases
        single { GetVersionUseCase(get()) }
        single { PrintHelpUseCase(get()) }
        single { RunTestUseCase(get(), get()) }

        single { MainInteractor(get(), get(), get(), get()) }
    }
}