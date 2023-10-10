package com.github.aivanovski.picoautomator

import com.github.aivanovski.picoautomator.FlowFactory.newFlow
import com.github.aivanovski.picoautomator.domain.entity.Duration.Companion.seconds
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.containsText
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.contentDesc
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.id
import com.github.aivanovski.picoautomator.domain.entity.ElementReference.Companion.text
import com.github.aivanovski.picoautomator.utils.OutputMatcher.matches
import com.github.aivanovski.picoautomator.utils.TestUtils.runAndCaptureOutput
import org.junit.jupiter.api.Test

class PicoAutomatorApiTest {

    @Test
    fun `check basic api calls`() {
        // arrange
        val flow = newFlow("Test Flow") {
            launch("org.wikipedia")
            assertVisible(text("Search Wikipedia"))
            tapOn(text("Search Wikipedia"))
            assertVisible(id("search_src_text"))
            inputText(element = text("Search Wikipedia"), "Dunning")
            assertVisible(text("Dunning–Kruger effect"))
            tapOn(contentDesc("Navigate up"))
            waitUntil(text("More"), timeout = seconds(5))
            tapOn(text("More"))
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
            Step 1: Launch app: org.wikipedia - SUCCESS
            Step 2: Assert is visible: [text = Search Wikipedia] - SUCCESS
            Step 3: Tap on element: [text = Search Wikipedia] - SUCCESS
            Step 4: Assert is visible: [id = search_src_text] - SUCCESS
            Step 5: Input text: [Dunning] into [text = Search Wikipedia] - SUCCESS
            Step 6: Assert is visible: [text = Dunning–Kruger effect] - SUCCESS
            Step 7: Tap on element: [desc = Navigate up] - SUCCESS
            Step 8: Wait for element: [text = More], timeout = 5 seconds, step = 1000 millis - SUCCESS
            Step 9: Tap on element: [text = More] - SUCCESS
            Step 10: Assert is visible: [text = Log in / join Wikipedia] - SUCCESS
            Step 11: Tap on element: [text = Settings] - SUCCESS
            Step 12: Assert is visible: [text = Settings] - SUCCESS
            Step 13: Press back - SUCCESS
            Step 14: Is visible: [[text = Search Wikipedia]] - SUCCESS
            Flow 'Test Flow' finished successfully
        """
    }

    @Test
    fun `references should work`() {
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
            Step 1: Launch app: org.wikipedia - SUCCESS
            Step 2: Assert is visible: [[id = main_view_pager],[desc = More],[text = Explore],[has text = Wikipedia]] - SUCCESS
            Flow 'Test Flow' finished successfully
        """
    }
}