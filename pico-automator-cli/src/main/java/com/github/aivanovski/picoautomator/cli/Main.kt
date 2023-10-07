package com.github.aivanovski.picoautomator.cli

import com.github.aivanovski.picoautomator.cli.di.GlobalInjector.get
import com.github.aivanovski.picoautomator.cli.di.KoinModule
import com.github.aivanovski.picoautomator.cli.domain.ErrorHandler
import com.github.aivanovski.picoautomator.cli.domain.MainInteractor
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(KoinModule.appModule)
    }

    val interactor: MainInteractor = get()
    val errorHandler: ErrorHandler = get()

    val result = interactor.process(args)
    errorHandler.processIfLeft(result)
}