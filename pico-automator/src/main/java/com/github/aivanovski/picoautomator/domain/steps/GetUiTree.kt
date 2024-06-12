package com.github.aivanovski.picoautomator.domain.steps

import com.github.aivanovski.picoautomator.data.adb.AdbExecutor
import com.github.aivanovski.picoautomator.data.adb.command.GetUiTreeCommand
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode

internal class GetUiTree : ExecutableStepCommand<UiTreeNode>, FlakyFlowStep {

    override fun describe(): String {
        return "Get UI tree"
    }

    override fun execute(adbExecutor: AdbExecutor): Either<Exception, UiTreeNode> {
        val getUiTreeResult = adbExecutor.execute(GetUiTreeCommand())
        if (getUiTreeResult.isLeft()) {
            return getUiTreeResult.toLeft()
        }

        val rootNote = getUiTreeResult.unwrap()
        return Either.Right(rootNote)
    }
}