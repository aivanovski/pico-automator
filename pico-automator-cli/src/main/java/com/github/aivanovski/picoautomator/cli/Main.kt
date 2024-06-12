package com.github.aivanovski.picoautomator.cli

import com.github.aivanovski.picoautomator.cli.di.GlobalInjector.get
import com.github.aivanovski.picoautomator.cli.di.CliAppModule
import com.github.aivanovski.picoautomator.cli.domain.MainInteractor
import com.github.aivanovski.picoautomator.cli.entity.exception.TestExecutionException
import com.github.aivanovski.picoautomator.presentation.OutputWriter
import kotlin.system.exitProcess
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(CliAppModule.module)
    }

    val interactor: MainInteractor = get()
    val writer: OutputWriter = get()

    val result = interactor.process(args)
    if (result.isLeft()) {
        val exception = result.unwrapError()
        if (exception !is TestExecutionException) {
            writer.printStackTrace(result.unwrapError())
        }
        exitProcess(1)
    }
}
