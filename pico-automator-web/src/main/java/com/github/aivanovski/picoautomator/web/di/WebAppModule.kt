package com.github.aivanovski.picoautomator.web.di

import com.github.aivanovski.picoautomator.web.data.repository.FakeFlowRepository
import com.github.aivanovski.picoautomator.web.data.repository.FakeUserRepository
import com.github.aivanovski.picoautomator.web.data.repository.FlowRepository
import com.github.aivanovski.picoautomator.web.data.repository.UserRepository
import com.github.aivanovski.picoautomator.web.presentation.controller.LoginController
import com.github.aivanovski.picoautomator.web.domain.service.AuthService
import com.github.aivanovski.picoautomator.web.presentation.controller.FlowController
import com.github.aivanovski.picoautomator.web.presentation.controller.ProjectController
import com.github.aivanovski.picoautomator.data.resources.ResourceProvider
import com.github.aivanovski.picoautomator.data.resources.ResourceProviderImpl
import com.github.aivanovski.picoautomator.web.data.repository.FakeProjectRepository
import com.github.aivanovski.picoautomator.web.data.repository.ProjectRepository
import kotlin.math.sin
import org.koin.dsl.module

object WebAppModule {

    val module = module {
        // core
        single<ResourceProvider> { ResourceProviderImpl(WebAppModule::class) }

        // Repositories
        single<UserRepository> { FakeUserRepository() }
        single<FlowRepository> { FakeFlowRepository() }
        single<ProjectRepository> { FakeProjectRepository() }

        // Services
        single { AuthService(get()) }

        // Controllers
        single { LoginController(get()) }
        single { FlowController(get(), get()) }
        single { ProjectController(get()) }
    }
}