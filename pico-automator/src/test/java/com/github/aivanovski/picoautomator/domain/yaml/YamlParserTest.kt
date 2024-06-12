package com.github.aivanovski.picoautomator.domain.yaml

import com.github.aivanovski.picoautomator.domain.entity.Either
import com.github.aivanovski.picoautomator.domain.newapi.entity.Flow
import com.github.aivanovski.picoautomator.domain.newapi.entity.FlowStep
import com.github.aivanovski.picoautomator.domain.newapi.entity.UiElementSelector
import com.github.aivanovski.picoautomator.util.StringUtils.NEW_LINE
import io.kotest.matchers.shouldBe
import java.lang.StringBuilder
import org.junit.jupiter.api.Test

class YamlParserTest {

    // TODO: add pressKey test
    // TODO: add waitUntil test

    @Test
    fun parse() {
        val result = YamlParser().parse(YAML_CONTENT)
        val expected = Flow(
            name = NAME,
            steps = ALL_STEPS
        )

        result shouldBe Either.Right(expected)
    }

    companion object {
        private const val NAME = "flow-name"
        private const val TEXT = "element-text"
        private const val ID = "element-id"
        private const val CONTENT_DESCRIPTION = "content-description"
        private const val HAS_TEXT = "has-text"
        private const val INPUT_TEXT = "input-text"
        private const val PACKAGE_NAME = "com.android.app"
        private const val BROADCAST_ACTION = "com.android.app.BroadcastReceiver"
        private const val BROADCAST_KEY = "broadcast-key"
        private const val BROADCAST_VALUE = "broadcast-value"

        private fun UiElementSelector.asList(): List<UiElementSelector> {
            return listOf(this)
        }

        private val SEND_BROADCAST_STEPS = listOf(
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = mapOf(BROADCAST_KEY to BROADCAST_VALUE)
            )
        )

        private val LAUNCH_STEPS = listOf(
            FlowStep.Launch(PACKAGE_NAME)
        )

        private val ASSERT_VISIBLE_STEPS = listOf(
            FlowStep.AssertVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.id(ID).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.containsText(HAS_TEXT).asList()
            )
        )

        private val ASSERT_NOT_VISIBLE_STEPS = listOf(
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.id(ID).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.containsText(HAS_TEXT).asList()
            )
        )

        private val TAP_ON_STEPS = listOf(
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.id(ID)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.containsText(HAS_TEXT)
            )
        )

        private val LONG_TAP_ON_STEPS = listOf(
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.id(ID),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.containsText(HAS_TEXT),
                isLong = true
            )
        )

        private val INPUT_TEXT_STEPS = listOf(
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = null
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.id(ID)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.containsText(HAS_TEXT)
            )
        )

        private val ALL_STEPS = SEND_BROADCAST_STEPS + LAUNCH_STEPS +
            ASSERT_VISIBLE_STEPS + ASSERT_NOT_VISIBLE_STEPS +
            TAP_ON_STEPS + LONG_TAP_ON_STEPS +
            INPUT_TEXT_STEPS

        private val NAME_BLOCK = """
            - name: $NAME
        """.trimIndent()

        private val SEND_BROADCAST_BLOCK = """
            - sendBroadcast: $PACKAGE_NAME/$BROADCAST_ACTION
              data:
                - key: $BROADCAST_KEY
                  value: $BROADCAST_VALUE
        """.trimIndent()

        private val LAUNCH_BLOCK = """
            - launch: $PACKAGE_NAME
        """.trimIndent()

        private val ASSERT_VISIBLE_BLOCK = """
            - assertVisible: $TEXT

            - assertVisible:
                text: $TEXT

            - assertVisible:
                contentDescription: $CONTENT_DESCRIPTION

            - assertVisible:
                id: $ID

            - assertVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        private val ASSERT_NOT_VISIBLE_BLOCK = """
            - assertNotVisible: $TEXT

            - assertNotVisible:
                text: $TEXT

            - assertNotVisible:
                contentDescription: $CONTENT_DESCRIPTION

            - assertNotVisible:
                id: $ID

            - assertNotVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        private val TAP_ON_BLOCK = """
            - tapOn: $TEXT

            - tapOn:
                text: $TEXT

            - tapOn:
                contentDescription: $CONTENT_DESCRIPTION

            - tapOn:
                id: $ID

            - tapOn:
                hasText: $HAS_TEXT
        """.trimIndent()

        private val LONG_TAP_ON_BLOCK = """
            - longTapOn: $TEXT

            - longTapOn:
                text: $TEXT

            - longTapOn:
                contentDescription: $CONTENT_DESCRIPTION

            - longTapOn:
                id: $ID

            - longTapOn:
                hasText: $HAS_TEXT
        """.trimIndent()

        private val INPUT_TEXT_BLOCK = """
            - inputText: $INPUT_TEXT

            - inputText:
                input: $INPUT_TEXT
                text: $TEXT

            - inputText:
                input: $INPUT_TEXT
                contentDescription: $CONTENT_DESCRIPTION

            - inputText:
                input: $INPUT_TEXT
                id: $ID

            - inputText:
                input: $INPUT_TEXT
                hasText: $HAS_TEXT
        """.trimIndent()

        private val YAML_CONTENT = YamlTextBuilder()
            .apply {
                appendBlock(NAME_BLOCK)
                appendBlock(SEND_BROADCAST_BLOCK)
                appendBlock(LAUNCH_BLOCK)
                appendBlock(ASSERT_VISIBLE_BLOCK)
                appendBlock(ASSERT_NOT_VISIBLE_BLOCK)
                appendBlock(TAP_ON_BLOCK)
                appendBlock(LONG_TAP_ON_BLOCK)
                appendBlock(INPUT_TEXT_BLOCK)
            }
            .toString()

        private class YamlTextBuilder {

            private val content = StringBuilder()

            fun appendBlock(block: String) {
                if (content.isNotEmpty()) {
                    content.append(NEW_LINE)
                }

                block.split("\n")
                    .filter { line -> line.isNotBlank() }
                    .forEach { line ->
                        content.append(line).append(NEW_LINE)
                    }
            }

            override fun toString(): String = content.toString()
        }
    }
}