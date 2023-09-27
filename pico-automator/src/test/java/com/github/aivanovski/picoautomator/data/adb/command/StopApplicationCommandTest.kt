package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.data.adb.command.StopApplicationCommand.Companion.COMMAND
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.util.StringUtils.EMPTY
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class StopApplicationCommandTest {

    @Test
    fun `execute should stop application`() {
        // arrange
        val expected = String.format(COMMAND, PACKAGE_NAME)
        val environment = mockk<AdbEnvironment>()
        every { environment.run(expected) } returns Either.Right(EMPTY)

        // act
        val result = StopApplicationCommand(PACKAGE_NAME).execute(environment)

        // assert
        result shouldBe Either.Right(Unit)
    }

    companion object {
        private const val PACKAGE_NAME = "package-name"
    }
}