package com.github.aivanovski.picoautomator.data.adb.command

import com.github.aivanovski.picoautomator.data.adb.AdbEnvironment
import com.github.aivanovski.picoautomator.data.adb.converters.convertToUiNode
import com.github.aivanovski.picoautomator.data.adb.entity.UiNodeEntity
import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.entity.UiTreeNode
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

internal class GetUiTreeCommand : AdbCommand<UiTreeNode> {

    override fun execute(environment: AdbEnvironment): Either<Exception, UiTreeNode> {
        val getDumpResult = getDumpFile(environment)
        if (getDumpResult.isLeft()) {
            return getDumpResult.toLeft()
        }

        return parseDumpFile(getDumpResult.unwrap())
    }

    private fun parseDumpFile(content: String): Either<Exception, UiTreeNode> {
        return try {
            val parser = SAXParserFactory.newInstance().newSAXParser()
            val handler = UiHierarchyHandler()

            parser.parse(InputSource(StringReader(content)), handler)

            val root = handler.root
                ?: return Either.Left(Exception("Unable to parse UI dump"))

            Either.Right(root.convertToUiNode())
        } catch (exception: SAXException) {
            Either.Left(exception)
        }
    }

    private fun getDumpFile(environment: AdbEnvironment): Either<Exception, String> {
        val attemptRange = 1..MAX_RETRY_COUNT
        for (attemptIdx in attemptRange) {
            val dumpResult = environment.run("shell uiautomator dump")
            if (dumpResult.isLeft()) {
                return dumpResult.toLeft()
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

    class UiHierarchyHandler : DefaultHandler() {

        var root: UiNodeEntity? = null

        private var currentNode: UiNodeEntity? = null
        private val stack = mutableListOf<UiNodeEntity>()

        override fun startElement(
            uri: String?,
            localName: String?,
            qName: String?,
            attributes: Attributes?
        ) {
            val node = UiNodeEntity()

            node.resourceId = attributes?.getValue("resource-id")
            node.className = attributes?.getValue("class")
            node.packageName = attributes?.getValue("package")
            node.text = attributes?.getValue("text")
            node.contentDescription = attributes?.getValue("content-desc")
            node.isFocused = attributes?.getValue("focused")?.toBoolean() ?: false
            node.bounds = attributes?.getValue("bounds")

            if (stack.isEmpty()) {
                root = node
            } else {
                currentNode = node
                stack.last().nodes.add(node)
            }

            stack.add(node)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            stack.removeLast()
            currentNode = if (stack.isEmpty()) {
                null
            } else {
                stack.last()
            }
        }
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
    }
}