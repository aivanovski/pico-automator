package com.github.aivanovski.picoautomator.utils

object OutputMatcher {

    infix fun String.matches(
        expectedOutput: String
    ) {
        val actualLines = this.trim()
            .lines()
            .map { line -> line.trim() }
            .filter { line -> line.isNotEmpty() }
            .map { line -> parseLine(line) }

        val expectedLines = expectedOutput.trim()
            .lines()
            .map { line -> line.trim() }
            .filter { line -> line.isNotEmpty() }
            .map { line -> parseLine(line) }

        var actualIdx = 0
        var expectedIdx = 0
        while (expectedIdx < expectedLines.size) {
            val expected = expectedLines[expectedIdx]
            val actual = actualLines.getOrNull(actualIdx)
                ?: throw AssertionError("Unable to match: '$expected'")

            if (isFailed(actual, expected)) {
                throw AssertionError("Failed: actual=$actual, expected=$expected")
            }

            if (isMatches(actual, expected)) {
                actualIdx++
                expectedIdx++
            } else {
                actualIdx++
            }
        }
    }

    private fun isFailed(actual: Line, expected: Line): Boolean {
        return expected is Line.FlowFinished &&
            (actual is Line.Step || actual is Line.Retry)
    }

    private fun isMatches(actual: Line, expected: Line): Boolean {
        if (actual == expected) {
            return true
        }

        return actual is Line.Retry &&
            expected is Line.Step &&
            actual.index == expected.index &&
            actual.command == expected.command &&
            actual.isSuccess == expected.isSuccess
    }

    private fun parseLine(line: String): Line {
        return when {
            line.startsWith("Select device:") -> Line.SelectDevice
            line.startsWith("Start flow '") -> parseFlowStartedLine(line)
            line.startsWith("Flow") -> parseFlowFinishedLine(line)
            line.startsWith("Step") -> parseStepLine(line)
            line.startsWith("Retry") -> parseStepLine(line)
            else -> Line.Unrecognized
        }
    }

    private fun parseFlowStartedLine(line: String): Line {
        if (!line.startsWith("Start flow '")) {
            return Line.Unrecognized
        }

        val nameStartIdx = line.indexOf("'")
        val nameEndIdx = line.lastIndexOf("'")
        if (nameStartIdx == -1 || nameEndIdx == -1) {
            return Line.Unrecognized
        }

        val name = line.nextValueBetween(0, "'")
            ?: return Line.Unrecognized

        return Line.FlowStarted(name)
    }

    private fun String.nextValueBetween(startIndex: Int, separator: String): String? {
        val startIdx = this.indexOf(separator, startIndex = startIndex)
        if (startIdx == -1) {
            return null
        }

        val endIdx = this.indexOf(separator, startIndex = startIdx + 1)
        if (endIdx == -1) {
            return null
        }

        return this.substring(startIdx + 1, endIdx)
    }

    private fun parseFlowFinishedLine(line: String): Line {
        if (!line.startsWith("Flow '")) {
            return Line.Unrecognized
        }

        val name = line.nextValueBetween(0, "'")
            ?: return Line.Unrecognized

        val isFinishedSuccessfully = line.contains("finished successfully")

        return Line.FlowFinished(name, isFinishedSuccessfully)
    }

    private fun parseStepLine(line: String): Line {
        if (!line.startsWith("Step") && !line.startsWith("Retry")) {
            return Line.Unrecognized
        }

        val colonIdx = line.indexOf(":")
        if (colonIdx == -1) {
            return Line.Unrecognized
        }

        val stepIndex = line.substring(0, colonIdx)
            .trim()
            .split(" ")
            .getOrNull(1)
            ?: return Line.Unrecognized

        if (!line.contains("- SUCCESS") && !line.contains("- FAILED")) {
            return Line.Unrecognized
        }

        val commandStartIdx = colonIdx + 1
        val commandEndIdx = if (line.contains("SUCCESS")) {
            line.indexOf("- SUCCESS") - 1
        } else {
            line.indexOf("- FAILED") - 1
        }

        return if (line.startsWith("Step")) {
            Line.Step(
                index = stepIndex.toInt(),
                command = line.substring(commandStartIdx, commandEndIdx).trim(),
                isSuccess = line.contains("SUCCESS")
            )
        } else {
            Line.Retry(
                index = stepIndex.toInt(),
                command = line.substring(commandStartIdx, commandEndIdx).trim(),
                isSuccess = line.contains("SUCCESS")
            )
        }
    }

    sealed class Line {

        object SelectDevice : Line()
        object Unrecognized : Line()

        data class Step(
            val index: Int,
            val command: String,
            val isSuccess: Boolean
        ) : Line()

        data class Retry(
            val index: Int,
            val command: String,
            val isSuccess: Boolean
        ) : Line()

        data class FlowStarted(
            val name: String
        ) : Line()

        data class FlowFinished(
            val name: String,
            val isSuccess: Boolean
        ) : Line()
    }
}