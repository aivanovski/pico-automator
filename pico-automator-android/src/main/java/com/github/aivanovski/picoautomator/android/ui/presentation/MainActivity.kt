package com.github.aivanovski.picoautomator.android.ui.presentation

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
import com.github.aivanovski.picoautomator.android.ui.di.GlobalInjector.inject
import com.github.aivanovski.picoautomator.android.ui.domain.FlowInteractor
import com.github.aivanovski.picoautomator.android.ui.presentation.core.theme.PicoautomatorTheme
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
                    onStartDriverClicked = { startDriver() },
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
            interactor.initFlow()
        }
    }

    private fun startDriver() {
        // Timber.d("starting driver: ")
        //
        // val instrumentations = packageManager.queryInstrumentation(this.packageName, 0)
        // Timber.d("instrumentations: $instrumentations")
        //
        // if (instrumentations.isNotEmpty()) {
        //
        //     val arguments = Bundle()
        //     arguments.putString(
        //         "class",
        //         "com.github.aivanovski.picoautomator.android.driver.DriverService"
        //     )
        //
        //     val ins = instrumentations.first()
        //     val cn = ComponentName(ins.packageName, ins.name)
        //     // val cn = ComponentName(
        //     //     "com.github.aivanovski.picoautomator.android.driver.test",
        //     //     "androidx.test.runner.AndroidJUnitRunner"
        //     // )
        //
        //     val result = startInstrumentation(cn, null, arguments)
        //     Timber.d("result=$result, cn=$cn")
        // } else {
        //     Timber.d("No instrumentations")
        // }
    }

    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323
    }
}

@Composable
fun TestScreen(
    onStartDriverClicked: () -> Unit,
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
        // Button(
        //     onClick = onStartDriverClicked,
        // ) {
        //     Text(text = "Start driver")
        // }

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
                onStartDriverClicked = {},
                onStopTestClicked = {},
                onStartTestClicked = {},
                onPermissionClicked = {}
            )
        }
    }
}