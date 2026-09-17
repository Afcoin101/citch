package com.example.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CameraTargetType {
    COOK_AVATAR,
    DISH_PHOTO
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CookCameraDialog(
    targetType: CameraTargetType = CameraTargetType.COOK_AVATAR,
    title: String = if (targetType == CameraTargetType.COOK_AVATAR) "Cook Profile Photo" else "Signature Dish Photo",
    subtitle: String = if (targetType == CameraTargetType.COOK_AVATAR) "Take a friendly profile photo so food lovers recognize you!" else "Capture appetizing plating of your signature meal",
    onPhotoCaptured: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // System gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Copy gallery image to local storage to ensure persistent file URI access
            val savedPath = copyUriToInternalStorage(context, uri, if (targetType == CameraTargetType.COOK_AVATAR) "cook_avatar" else "dish_photo")
            onPhotoCaptured(savedPath)
            onDismiss()
        }
    }

    // System camera capture preview fallback
    val systemCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedPath = saveBitmapToFile(context, bitmap, if (targetType == CameraTargetType.COOK_AVATAR) "cook_avatar" else "dish_photo")
            onPhotoCaptured(savedPath)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            if (cameraPermissionState.status.isGranted) {
                CookLiveCameraContent(
                    targetType = targetType,
                    title = title,
                    subtitle = subtitle,
                    onPhotoConfirmed = { photoUriString ->
                        onPhotoCaptured(photoUriString)
                        onDismiss()
                    },
                    onOpenGallery = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onDismiss = onDismiss
                )
            } else {
                CookCameraPermissionContent(
                    shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
                    targetType = targetType,
                    onRequestPermission = {
                        cameraPermissionState.launchPermissionRequest()
                    },
                    onOpenSystemCamera = {
                        systemCameraLauncher.launch(null)
                    },
                    onOpenGallery = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun CookLiveCameraContent(
    targetType: CameraTargetType,
    title: String,
    subtitle: String,
    onPhotoConfirmed: (String) -> Unit,
    onOpenGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Default to front camera for cook selfie profile avatars, back camera for dishes
    var lensFacing by remember {
        mutableIntStateOf(
            if (targetType == CameraTargetType.COOK_AVATAR) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
        )
    }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    LaunchedEffect(flashMode) {
        imageCapture.flashMode = flashMode
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (e: Exception) {
                Log.w("CookCameraDialog", "Failed to unbind camera on dispose", e)
            }
        }
    }

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(previewViewRef, lensFacing, lifecycleOwner) {
        val pv = previewViewRef ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(pv.surfaceProvider)
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CookCameraDialog", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CameraX Live Preview with TextureView (COMPATIBLE mode avoids SurfaceView BufferQueue errors)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }.also { previewViewRef = it }
            },
            update = { /* Stable preview - prevent rebinding on recomposition */ },
            modifier = Modifier.fillMaxSize().testTag("cook_camera_preview")
        )

        // Visual Framing Guide Overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (targetType == CameraTargetType.COOK_AVATAR) {
                // Circular guide for cook headshot
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                )
            } else {
                // Rectangular guide for dishes
                Box(
                    modifier = Modifier
                        .size(width = 300.dp, height = 220.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                )
            }
        }

        // Top Navigation & Settings Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cook_camera_close")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Camera", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (lensFacing == CameraSelector.LENS_FACING_FRONT) "Front / Selfie Lens" else "Main / Back Lens",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Flash Toggle
                IconButton(
                    onClick = {
                        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                            ImageCapture.FLASH_MODE_ON
                        } else {
                            ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("cook_camera_flash_toggle")
                ) {
                    Icon(
                        imageVector = if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (flashMode == ImageCapture.FLASH_MODE_ON) Color.Yellow else Color.White
                    )
                }

                // Flip Front / Back Camera
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("cook_camera_flip_lens")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip Camera",
                        tint = Color.White
                    )
                }
            }
        }

        // Subtitle Tip Banner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp, start = 24.dp, end = 24.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = subtitle,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        // Bottom Controls Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(vertical = 24.dp, horizontal = 32.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Picker Button
                IconButton(
                    onClick = onOpenGallery,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.DarkGray, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        .testTag("cook_camera_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Choose from Gallery",
                        tint = Color.White
                    )
                }

                // Shutter Capture Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .background(if (isCapturing) Color.Gray else Color.White.copy(alpha = 0.2f))
                        .clickable(enabled = !isCapturing) {
                            isCapturing = true
                            takeCookPhoto(
                                context = context,
                                imageCapture = imageCapture,
                                targetType = targetType,
                                executor = ContextCompat.getMainExecutor(context),
                                onImageSaved = { uri ->
                                    isCapturing = false
                                    capturedUri = uri
                                },
                                onError = { exc ->
                                    isCapturing = false
                                    Toast.makeText(context, "Capture failed: ${exc.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        .testTag("cook_camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (targetType == CameraTargetType.COOK_AVATAR) MaterialTheme.colorScheme.primary else Color.White)
                    )
                }

                // Placeholder / Cancel button for symmetry
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.DarkGray, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = Color.White
                    )
                }
            }
        }

        // Review & Confirm Overlay when photo captured
        AnimatedVisibility(
            visible = capturedUri != null,
            modifier = Modifier.fillMaxSize()
        ) {
            capturedUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (targetType == CameraTargetType.COOK_AVATAR) "Review Cook Profile Photo" else "Review Dish Photo",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Looking great! Do you want to use this photo?",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Captured Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { capturedUri = null },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("cook_camera_retake_button"),
                                shape = RoundedCornerShape(26.dp),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onPhotoConfirmed(uri.toString())
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("cook_camera_confirm_button"),
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use Photo", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CookCameraPermissionContent(
    shouldShowRationale: Boolean,
    targetType: CameraTargetType,
    onRequestPermission: () -> Unit,
    onOpenSystemCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (targetType == CameraTargetType.COOK_AVATAR) "Cook Profile Photo Camera" else "Signature Dish Camera",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (shouldShowRationale) {
                "Camera access lets you capture photos directly so clients can see you and your delicious home cooking!"
            } else {
                "Grant camera access to take your chef profile avatar and dish photos directly inside the app."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("enable_camera_permission_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enable Camera", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenGallery,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("cook_permission_gallery_button"),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Choose from Gallery Instead", color = Color.White, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cancel",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { onDismiss() }
                .padding(8.dp)
        )
    }
}

private fun takeCookPhoto(
    context: Context,
    imageCapture: ImageCapture,
    targetType: CameraTargetType,
    executor: java.util.concurrent.Executor,
    onImageSaved: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val prefix = if (targetType == CameraTargetType.COOK_AVATAR) "COOK_AVATAR" else "DISH"
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val photoFile = File(context.filesDir, "${prefix}_${timestamp}.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageSaved(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap, prefix: String): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(context.filesDir, "${prefix}_${timestamp}_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    return Uri.fromFile(file).toString()
}

fun copyUriToInternalStorage(context: Context, sourceUri: Uri, prefix: String): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(context.filesDir, "${prefix}_${timestamp}.jpg")
    context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return Uri.fromFile(file).toString()
}
