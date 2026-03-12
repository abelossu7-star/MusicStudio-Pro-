package com.musicstudio.pro.services.video

import android.content.Context
import android.media.MediaRecorder
import java.io.File

/**
 * Simple recording service that uses MediaRecorder.
 *
 * NOTE: This implementation records audio (microphone) only. For true video capture,
 * integrate CameraX (VideoCapture use case) or Camera2 + MediaRecorder along with a view surface.
 */
class VideoRecorderService(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(outputPath: String) {
        stopRecording()

        outputFile = File(outputPath).apply {
            parentFile?.mkdirs()
            if (exists()) delete()
        }

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.run {
                stop()
                reset()
                release()
            }
        } catch (_: Throwable) {
            // ignore
        } finally {
            mediaRecorder = null
        }
    }
}
