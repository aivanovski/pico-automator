package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.extensions.indexOf

class StartApplicationCommand(
    private val packageName: String
) : AdbCommand<Unit> {

    override fun execute(environment: AdbEnvironment): Either<Exception, Unit> {
        val packageDumpResult = environment.run("shell dumpsys package")
        if (packageDumpResult.isLeft()) {
            return packageDumpResult.mapToLeft()
        }

        val dump = packageDumpResult.unwrap()
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val tableStartIdx = dump.indexOf(startIdx = 0) {
            it == "Activity Resolver Table:"
        }
        if (tableStartIdx == -1) {
            return Either.Left(Exception("Not found: 'Activity Resolver Table:'"))
        }

        val tableEndIdx = dump.indexOf(startIdx = tableStartIdx + 1) {
            it == "Receiver Resolver Table:"
        }
        if (tableEndIdx == -1) {
            return Either.Left(Exception("Not found: 'Receiver Resolver Table:'"))
        }

        val actionStartIdx = dump.indexOf(startIdx = tableStartIdx + 1) {
            it == "android.intent.action.MAIN:"
        }
        if (actionStartIdx == -1) {
            return Either.Left(Exception("Not found: 'android.intent.action.MAIN:'"))
        }

        val actionEndIdx = dump.indexOf(startIdx = actionStartIdx + 1) {
            it.startsWith("android.intent.action")
        }
        if (actionEndIdx == -1) {
            return Either.Left(Exception("Not found: 'android.intent.action'"))
        }

        val activityName = dump.subList(actionStartIdx + 1, actionEndIdx)
            .filter { it.contains(packageName) }
            .map { it.split(" ").last() }
            .firstOrNull()
            ?: return Either.Left(Exception("Unable to find MAIN activity for package: $packageName"))

        val result =
            environment.run("shell am start -a android.intent.action.MAIN -n $activityName")

        return result.mapWith(Unit)
    }
}