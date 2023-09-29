package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.extensions.indexOf

internal class StartApplicationCommand(
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

        val activities = dump.subList(actionStartIdx + 1, actionEndIdx)
            .filter { it.contains(packageName) }
            .mapNotNull { parseActivity(it) }

        val activity = findActivityForPackage(packageName, activities)
            ?: return Either.Left(
                Exception("Unable to find MAIN activity for package: $packageName")
            )

        val result = environment.run(formatStartMainActivityCommand(activity))

        return result.mapWith(Unit)
    }

    private fun formatStartMainActivityCommand(
        activity: ActivityComponent
    ): String {
        val activityName = activity.packageName + "/" + activity.activityName
        return String.format(
            "shell am start -a android.intent.action.MAIN -n %s",
            activityName
        )
    }

    private fun parseActivity(
        dumpLine: String
    ): ActivityComponent? {
        val values = dumpLine.split(" ")
        if (values.size != 2) {
            return null
        }

        val componentName = values.last().trim()
        if (componentName.isEmpty()) {
            return null
        }

        val components = componentName.split("/")
        if (components.size != 2) {
            return null
        }

        return ActivityComponent(
            packageName = components.first().trim(),
            activityName = components.last().trim()
        )
    }

    private fun findActivityForPackage(
        packageName: String,
        activities: List<ActivityComponent>
    ): ActivityComponent? {
        val nonLibraryActivities = activities.filter { activity ->
            !activity.activityName.startsWith("android")
        }

        val startedWithPackageActivities = nonLibraryActivities.filter { activity ->
            val prefix = activity.activityName.commonPrefixWith(packageName)
            prefix.isNotEmpty()
        }
        if (startedWithPackageActivities.isNotEmpty()) {
            return startedWithPackageActivities.firstOrNull()
        }

        return nonLibraryActivities.firstOrNull()
    }

    data class ActivityComponent(
        val packageName: String,
        val activityName: String
    )
}