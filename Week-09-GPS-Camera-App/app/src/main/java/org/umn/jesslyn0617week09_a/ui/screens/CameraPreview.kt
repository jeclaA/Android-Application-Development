package org.umn.jesslyn0617week09_a.ui.screens

import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreview(
    lifecycleOwner: LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    DisposableEffect(previewView, lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var cameraProvider: ProcessCameraProvider? = null
        cameraProviderFuture.addListener(
            {
                try {
                    val provider = cameraProviderFuture.get()
                    cameraProvider = provider

                    val targetRotation = previewView.display?.rotation
                        ?: Surface.ROTATION_0

                    val preview = Preview.Builder().setTargetRotation(targetRotation)
                        .build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                    val capture = ImageCapture.Builder()
                        .setTargetRotation(targetRotation)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,
                        preview, capture
                    )
                    onImageCaptureReady(capture)
                } catch (e: Exception) {
                    onError("Failed to open camera: ${e.message}")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
        onDispose {
            cameraProvider?.unbindAll()
        }
    }
    AndroidView(
        factory = {previewView},
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
    )
}
