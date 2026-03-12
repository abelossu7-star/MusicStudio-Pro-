package com.musicstudio.pro.features.upload.upload_video

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.camera.view.PreviewView
import com.musicstudio.pro.data.models.UiState
import java.io.File

@Composable
fun UploadVideoScreen(viewModel: UploadVideoViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var recordingFile by remember { mutableStateOf<File?>(null) }
    var recording by remember { mutableStateOf<androidx.camera.video.Recording?>(null) }

    val uploadState by viewModel.uploadState.collectAsState()

    val previewView = remember { PreviewView(context) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                bindCamera(context, lifecycleOwner, previewView)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
            bindCamera(context, lifecycleOwner, previewView)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Upload Video")

        AndroidView({ previewView }, modifier = Modifier
            .fillMaxWidth()
            .height(240.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                if (isRecording) {
                    recording?.stop()
                    isRecording = false
                } else {
                    val file = File(context.cacheDir, "video_${System.currentTimeMillis()}.mp4")
                    recordingFile = file
                    recording = startRecording(context, file)
                    isRecording = true
                }
            }) {
                Text(text = if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(onClick = {
                recordingFile?.let { file ->
                    viewModel.uploadVideo(Uri.fromFile(file))
                }
            }, enabled = recordingFile != null && !isRecording) {
                Text(text = "Upload Recording")
            }
        }

        when (uploadState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
            is UiState.Error -> Text(text = (uploadState as UiState.Error).message, color = androidx.compose.ui.graphics.Color.Red)
            is UiState.Success -> Text(text = (uploadState as UiState.Success<String>).data, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

private fun bindCamera(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.SD))
            .build()
        val videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                videoCapture
            )
        } catch (_: Exception) {
            // ignore
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun startRecording(context: Context, file: File): androidx.camera.video.Recording? {
    val recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.SD))
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)
    val outputOptions = FileOutputOptions.Builder(file).build()

    return videoCapture.output
        .prepareRecording(context, outputOptions)
        .start(ContextCompat.getMainExecutor(context)) { event ->
            if (event is VideoRecordEvent.Finalize) {
                // Recording finalized
            }
        }
}
