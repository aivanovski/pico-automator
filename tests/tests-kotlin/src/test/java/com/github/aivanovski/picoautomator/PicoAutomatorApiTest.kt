package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.FlowFactory.newFlow
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.millis
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.seconds
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.containsText
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.contentDesc
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.id
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.text
import com.github.aivanovski.picoautomator.extensions.hasElement
import com.github.aivanovski.picoautomator.utils.OutputMatcher.matches
import com.github.aivanovski.picoautomator.utils.TestUtils.runAndCaptureOutput
import org.junit.jupiter.api.Test

class PicoAutomatorApiTest {

    private fun clearStateAndSkipIntro() {
        val flow = newFlow("Clear State and Skip Intro") {
            launch("org.wikipedia", isClearState = true)

            if (isVisible(containsText("The Free Encyclopedia"))) {
                tapOn(text("Skip"))
                assertVisible(text("Search Wikipedia"))
            }
        }

        val output = runAndCaptureOutput(flow)

        output matches """
            Select device: %s
            Start flow 'Clear State and Skip Intro'
            Step 1: Launch app: package name = org.wikipedia, clear state = true - SUCCESS
            Step 2: Is visible: [has text = The Free Encyclopedia] - SUCCESS
            Step 3: Tap on element: [text = Skip] - SUCCESS
            Step 4: Assert is visible: [text = Search Wikipedia] - SUCCESS
            Flow 'Clear State and Skip Intro' finished successfully
        """.trimIndent()
    }

    @Test
    fun `check basic api calls`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Test Flow") {
            launch("org.wikipedia")
            assertVisible(text("Search Wikipedia"))
            tapOn(text("Search Wikipedia"))
            assertVisible(id("search_src_text"))
            inputText(element = text("Search Wikipedia"), "Dunning")
            assertVisible(text("Dunning–Kruger effect"))
            tapOn(contentDesc("Navigate up"))
            waitUntil(contentDesc("More"), timeout = seconds(5))
            tapOn(contentDesc("More"))
            assertVisible(text("Log in / join Wikipedia"))
            tapOn(text("Settings"))
            assertVisible(text("Settings"))
            pressBack()
            isVisible(text("Search Wikipedia"))
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: %s
            Start flow 'Test Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Assert is visible: [text = Search Wikipedia] - SUCCESS
            Step 3: Tap on element: [text = Search Wikipedia] - SUCCESS
            Step 4: Assert is visible: [id = search_src_text] - SUCCESS
            Step 5: Input text: [Dunning] into [text = Search Wikipedia] - SUCCESS
            Step 6: Assert is visible: [text = Dunning–Kruger effect] - SUCCESS
            Step 7: Tap on element: [desc = Navigate up] - SUCCESS
            Step 8: Wait for element: [desc = More], timeout = 5 seconds, step = 1000 millis - SUCCESS
            Step 9: Tap on element: [desc = More] - SUCCESS
            Step 10: Assert is visible: [text = Log in / join Wikipedia] - SUCCESS
            Step 11: Tap on element: [text = Settings] - SUCCESS
            Step 12: Assert is visible: [text = Settings] - SUCCESS
            Step 13: Press back - SUCCESS
            Step 14: Is visible: [text = Search Wikipedia] - SUCCESS
            Flow 'Test Flow' finished successfully
        """
    }

    @Test
    fun `references should work`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Test Flow") {
            launch("org.wikipedia")

            assertVisible(
                listOf(
                    id("main_view_pager"),
                    contentDesc("More"),
                    text("Explore"),
                    containsText("Wikipedia")
                )
            )
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: %s
            Start flow 'Test Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Assert is visible: [[id = main_view_pager], [desc = More], [text = Explore], [has text = Wikipedia]] - SUCCESS
            Flow 'Test Flow' finished successfully
        """
    }

    @Test
    fun `sleep should work`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Sleep Flow") {
            launch("org.wikipedia")

            tapOn(text("Search Wikipedia"))
            val maxSleepCount = 2
            for (sleepIdx in 1..maxSleepCount) {
                sleep(millis(1500))

                val tree = getUiTree()
                when {
                    tree.hasElement(text("Recent searches:")) ||
                        tree.hasElement(containsText("Search Wikipedia in more languages")) -> {
                        complete("Complete message")
                    }

                    sleepIdx == maxSleepCount -> {
                        fail("Failure message")
                    }
                }
            }
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: %s
            Start flow 'Sleep Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Tap on element: [text = Search Wikipedia] - SUCCESS
            Step 3: Sleep 1500 millis - SUCCESS
            Step 4: Get UI tree - SUCCESS
            Flow 'Sleep Flow' finished successfully: Complete message
        """.trimIndent()
    }

    @Test
    fun `isVisible should work`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Is Visible Flow") {
            launch("org.wikipedia")

            if (!isVisible(text("Featured article"))) {
                fail("Failed")
            }

            tapOn(text("Search Wikipedia"))

            if (isVisible(text("Featured article"))) {
                fail("Failed")
            }
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: %s
            Start flow 'Is Visible Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Is visible: [text = Featured article] - SUCCESS
            Step 3: Tap on element: [text = Search Wikipedia] - SUCCESS
            Step 4: Is visible: [text = Featured article] - SUCCESS
            Flow 'Is Visible Flow' finished successfully
        """.trimIndent()
    }

    @Test
    fun `taps should work`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Taps Flow") {
            launch("org.wikipedia")

            assertVisible(text("Featured article"))
            tapOn(id("view_featured_article_card_content_container"))
            assertVisible(id("page_web_view"))

            pressBack()

            assertVisible(text("Featured article"))
            longTapOn(id("view_featured_article_card_content_container"))
            assertVisible(listOf(text("Share link"), text("Copy link address")))
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: %s
            Start flow 'Taps Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Assert is visible: [text = Featured article] - SUCCESS
            Step 3: Tap on element: [id = view_featured_article_card_content_container] - SUCCESS
            Step 4: Assert is visible: [id = page_web_view] - SUCCESS
            Step 5: Press back - SUCCESS
            Step 6: Assert is visible: [text = Featured article] - SUCCESS
            Step 7: Long tap on element: [id = view_featured_article_card_content_container] - SUCCESS
            Step 8: Assert is visible: [[text = Share link], [text = Copy link address]] - SUCCESS
            Flow 'Taps Flow' finished successfully
        """.trimIndent()
    }

    @Test
    fun `assertions should work`() {
        clearStateAndSkipIntro()

        // arrange
        val flow = newFlow("Assertions Flow") {
            launch("org.wikipedia")

            assertVisible(contentDesc("Explore"))
            assertVisible(
                listOf(
                    contentDesc("Explore"),
                    contentDesc("Saved"),
                    contentDesc("Search"),
                    contentDesc("Edits"),
                    contentDesc("More")
                )
            )

            tapOn(contentDesc("More"))
            tapOn(text("Settings"))

            assertNotVisible(contentDesc("Explore"))
            assertNotVisible(
                listOf(
                    contentDesc("Explore"),
                    contentDesc("Saved"),
                    contentDesc("Search"),
                    contentDesc("Edits"),
                    contentDesc("More")
                )
            )
        }

        // act
        val output = runAndCaptureOutput(flow)

        // assert
        output matches """
            Select device: emulator-5554
            Start flow 'Assertions Flow'
            Step 1: Launch app: package name = org.wikipedia - SUCCESS
            Step 2: Assert is visible: [desc = Explore] - SUCCESS
            Step 3: Assert is visible: [[desc = Explore], [desc = Saved], [desc = Search], [desc = Edits], [desc = More]] - SUCCESS
            Step 4: Tap on element: [desc = More] - SUCCESS
            Step 5: Tap on element: [text = Settings] - SUCCESS
            Step 6: Assert is not visible: [desc = Explore] - SUCCESS
            Step 7: Assert is not visible: [[desc = Explore], [desc = Saved], [desc = Search], [desc = Edits], [desc = More]] - SUCCESS
            Flow 'Assertions Flow' finished successfully
        """.trimIndent()
    }
}