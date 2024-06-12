package com.github.aivanovski.picoautomator.android.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.picoautomator.android.di.GlobalInjector.inject
import com.github.aivanovski.picoautomator.android.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.presentation.core.theme.PicoautomatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val interactor: FlowInteractor by inject()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PicoautomatorTheme {
                TestScreen(
                    onFirstButtonClicked = { },
                    onStartTestClicked = { startTest() },
                    onStopTestClicked = { stopTest() },
                    onPermissionClicked = { checkPermission() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 123)
        }
    }

    private fun stopTest() {
    }

    private fun startTest() {
        scope.launch {
            interactor.parseAndAddToJobQueue()
        }
    }
}

@Composable
fun TestScreen(
    onFirstButtonClicked: () -> Unit,
    onStartTestClicked: () -> Unit,
    onStopTestClicked: () -> Unit,
    onPermissionClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {
        Button(
            onClick = onFirstButtonClicked,
        ) {
            Text(text = "Start local test")
        }

        Button(
            onClick = onStartTestClicked,
        ) {
            Text(text = "Start test")
        }

        Button(
            onClick = onStopTestClicked,
        ) {
            Text(text = "Stop test")
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = onPermissionClicked,
        ) {
            Text(text = "Window permission")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestScreenPreview() {
    PicoautomatorTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            TestScreen(
                onFirstButtonClicked = {},
                onStopTestClicked = {},
                onStartTestClicked = {},
                onPermissionClicked = {}
            )
        }
    }
}