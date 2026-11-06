package org.umn.jesslyn0617week09_a.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.impl.utils.Exif
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraScreen(){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }
    var permissionMessage by remember { mutableStateOf( "")}
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null)}
    var status by remember { mutableStateOf("Camera not started.")}
    var capturing by remember { mutableStateOf(false)}
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null)}
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(cameraExecutor) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        permissionMessage = if (granted) {
            ""
        } else {
            "Camera permission denied. Please grant camera permission. "
        }
    }
    val displayedBitmap = photoBitmap
    if(displayedBitmap != null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Image(
                bitmap = displayedBitmap.asImageBitmap(),
                contentDescription = "Captured Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(16.dp).size(48.dp).background(
                        color = Color(0xFF90B4FF),
                        shape = CircleShape
                    ).clickable{
                        photoBitmap = null
                        status = "Camera ready to take a photo.." },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u00D7", color = Color(0xFF173a70),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 32.sp
                )
            }
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text( text = "Camera and Photo", style = MaterialTheme.typography.headlineSmall)
        if(!hasCameraPermission) {
            Text(text = permissionMessage.ifBlank {
                "Camera permission is required."
            })
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                ).clickable {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Allow Camera Access",
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        } else {
            CameraPreview(
                lifecycleOwner = lifecycleOwner,
                onImageCaptureReady =  { capture ->
                    imageCapture = capture
                    status = "Camera ready to take a photo.."
                },
                onError = { message ->
                    status = message
                }
            )
            Text(text = status)
        Box(modifier = Modifier.size(76.dp).background(
            color = if(capturing) {
                Color.Gray
            } else {
                Color(0xFFE0E0E0)
            },
            shape = CircleShape
        ).padding(5.dp).background(
            color = if(capturing) {
                Color.Gray
            } else {
                Color(0xFF424242)
            },
            shape = CircleShape
        ).clickable(
            enabled = !capturing
        ){
            val capture = imageCapture
            if (capture == null) {
                status = "Preparing Camera..."
            } else {
                capturing = true
                status = "Capturing photo..."
                val photoFile = File(
                    context.cacheDir,
                    "jesslyn_${System.currentTimeMillis()}.jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                    photoFile).build()
                capture.takePicture(outputOptions,
                    cameraExecutor, object:
                    ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(
                            outputFileResults: ImageCapture.OutputFileResults) {
                            val bitmap = decodePotraitBitmap(photoFile)
                            Handler(Looper.getMainLooper()).post {
                                capturing = false
                                if(bitmap != null) {
                                    photoBitmap = bitmap
                                    status = "Photo captured seuccessfully.."
                                } else {
                                    status = "Failed to display photo.."
                                }
                            }
                        }
                        override fun onError(
                            exception: ImageCaptureException
                        ) {
                            Handler(Looper.getMainLooper()).post {
                                capturing = false
                                status = "Failed to capture photo.." +
                                        exception.message
                            }
                        }
                    }
                )
            }
        }, contentAlignment = Alignment.Center) {
        }
        }
    }
}

private fun decodePotraitBitmap(photoFile: File): Bitmap? {
    val originalBitmap =
        BitmapFactory.decodeFile(photoFile.absolutePath)
            ?: return null
    try {
        val exif = ExifInterface(photoFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()

        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.postScale(1f, -1f)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setValues(
                    floatArrayOf(
                        0f, 1f, 0f,
                        1f, 0f, 0f,
                        0f, 0f, 1f
                    )
                )
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setValues(
                    floatArrayOf(
                        0f, -1f, 0f,
                        -1f, 0f, 0f,
                        0f, 0f, 1f
                    )
                )
            }
        }
        var correctedBitmap = Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )

        if(correctedBitmap.width > correctedBitmap.height) {
            val potraitMatrix = Matrix().apply {
                postRotate(90f)
            }
            val potraitBitmap = Bitmap.createBitmap(
                correctedBitmap,
                0,
                0,
                correctedBitmap.width,
                correctedBitmap.height,
                potraitMatrix,
                true
            )
            if (potraitBitmap !== correctedBitmap) {
                correctedBitmap.recycle()
            }
            correctedBitmap = potraitBitmap
        }
        if(correctedBitmap !== originalBitmap) {
            originalBitmap.recycle()
        }
        return correctedBitmap
    } catch (e: Exception) {
        originalBitmap.recycle()
        return null
    }
}