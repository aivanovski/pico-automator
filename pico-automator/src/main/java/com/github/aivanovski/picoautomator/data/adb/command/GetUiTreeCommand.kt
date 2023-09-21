package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.data.adb.converters.convertToUiNode
import com.github.aivanovski.picoautomator.data.adb.entity.UiHierarchyEntity
import com.github.aivanovski.picoautomator.data.adb.entity.UiNodeEntity
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import java.io.ByteArrayInputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException

internal class GetUiTreeCommand : AdbCommand<UiTreeNode> {

    override fun execute(environment: AdbEnvironment): Either<Exception, UiTreeNode> {
        val getDumpResult = getDumpFile(environment)
        if (getDumpResult.isLeft()) {
            return getDumpResult.mapToLeft()
        }

        return parseDumpFile(getDumpResult.unwrap())
    }

    private fun parseDumpFile(content: String): Either<Exception, UiTreeNode> {
        return try {
            val data = JAXBContext.newInstance(UiHierarchyEntity::class.java)
                .createUnmarshaller()
                .unmarshal(ByteArrayInputStream(content.toByteArray())) as UiHierarchyEntity

            val root = UiNodeEntity()
                .apply {
                    nodes = data.nodes
                }

            Either.Right(root.convertToUiNode())
        } catch (exception: JAXBException) {
            Either.Left(exception)
        }
    }

    private fun getDumpFile(environment: AdbEnvironment): Either<Exception, String> {
        val attemptRange = 1..MAX_RETRY_COUNT
        for (attemptIdx in attemptRange) {
            val dumpResult = environment.run("shell uiautomator dump")
            if (dumpResult.isLeft()) {
                return dumpResult.mapToLeft()
            }

            val dumpMessage = dumpResult.unwrap()
            when {
                attemptIdx < attemptRange.last && dumpMessage.isEmpty() -> {
                    // TODO: move to logging
                    println("Get UI dump again")
                    continue
                }

                dumpMessage.contains("dumped to") -> {
                    break
                }

                else -> {
                    return Either.Left(Exception("Unable to dump UI: $dumpMessage"))
                }
            }
        }

        return environment.run("shell cat /sdcard/window_dump.xml")
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
    }
}