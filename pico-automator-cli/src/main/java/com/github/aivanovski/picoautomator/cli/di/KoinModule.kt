package com.github.aivanovski.picoautomator.cli.di

import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProvider
import com.github.aivanovski.picoautomator.cli.data.filesystem.FileProviderImpl
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProvider
import com.github.aivanovski.picoautomator.cli.data.resource.ResourceProviderImpl
import com.github.aivanovski.picoautomator.cli.domain.ErrorHandler
import com.github.aivanovski.picoautomator.cli.domain.MainInteractor
import com.github.aivanovski.picoautomator.cli.domain.argument.ArgumentParser
import com.github.aivanovski.picoautomator.cli.domain.usecases.GetVersionUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.PrintTestExecutionResultUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.RunTestUseCase
import com.github.aivanovski.picoautomator.cli.domain.usecases.RunTestsUseCase
import com.github.aivanovski.picoautomator.presentation.ColoredOutputWriter
import com.github.aivanovski.picoautomator.presentation.OutputWriter
import com.github.aivanovski.picoautomator.presentation.StandardColoredOutputWriter
import com.github.aivanovski.picoautomator.presentation.StandardOutputWriter
import org.koin.dsl.module

object KoinModule {
    val appModule = module {
        single<FileProvider> { FileProviderImpl() }
        single<ResourceProvider> { ResourceProviderImpl() }
        single { ArgumentParser(get()) }
        single<OutputWriter> { StandardOutputWriter() }
        single<ColoredOutputWriter> { StandardColoredOutputWriter(get()) }
        single { ErrorHandler(get()) }

        // use cases
        single { GetVersionUseCase(get()) }
        single { PrintHelpUseCase(get()) }
        single { RunTestUseCase(get(), get(), get()) }
        single { RunTestsUseCase(get(), get()) }
        single { PrintTestExecutionResultUseCase(get()) }

        single { MainInteractor(get(), get(), get(), get(), get()) }
    }
}