package com.musicstudio.pro.features.studio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StudioScreen(viewModel: StudioViewModel = hiltViewModel()) {
    val prompt = viewModel.prompt
    val lyrics = viewModel.lyrics
    val beatUrl = viewModel.beatUrl
    val voiceId = viewModel.voiceId
    val ttsText = viewModel.ttsText
    val voiceSampleLocalUri = viewModel.voiceSampleLocalUri
    val voiceSampleRemoteUrl = viewModel.voiceSampleRemoteUrl
    val recordedFileUri = viewModel.recordedFileUri
    val isRecording = viewModel.isRecording
    val clonedVoiceUrl = viewModel.clonedVoiceUrl
    val statusMessage = viewModel.statusMessage
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "AI Studio")

        OutlinedTextField(
            value = prompt,
            onValueChange = viewModel::onPromptChanged,
            label = { Text("Enter prompt (lyrics/beat)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = viewModel::generateLyrics, modifier = Modifier.weight(1f)) {
                Text(text = "Generate Lyrics")
            }
            Button(onClick = viewModel::generateBeat, modifier = Modifier.weight(1f)) {
                Text(text = "Generate Beat")
            }
        }

        Button(onClick = viewModel::generateSong, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Generate Song")
        }

        lyrics?.let {
            Text(text = "Lyrics:\n$it")
        }

        beatUrl?.let {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Beat URL: $it")
                Button(onClick = viewModel::playBeat, modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = "Play Beat")
                }
            }
        }

        Text(text = "Voice synthesis")

        OutlinedTextField(
            value = voiceId,
            onValueChange = viewModel::onVoiceIdChanged,
            label = { Text("ElevenLabs voice ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ttsText,
            onValueChange = viewModel::onTtsTextChanged,
            label = { Text("Text to synthesize") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = viewModel::synthesizeVoice, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Synthesize & Play")
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(text = "Voice cloning")

        val pickAudioLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { viewModel.uploadVoiceSample(it.toString()) }
        }

        Text(text = "Record a voice sample")
        Button(
            onClick = {
                if (isRecording) viewModel.stopRecording() else viewModel.startRecording()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }

        recordedFileUri?.let {
            Text(text = "Recorded file: $it")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.playRecording() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Play" )
                }
                Button(
                    onClick = { viewModel.stopPlayback() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Stop")
                }
            }
            Button(
                onClick = { viewModel.uploadVoiceSample(it) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(text = "Upload Recording")
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(text = "Voice cloning")
        Button(onClick = { pickAudioLauncher.launch("audio/*") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Pick voice sample")
        }

        voiceSampleLocalUri?.let {
            Text(text = "Selected sample: $it")
        }

        voiceSampleRemoteUrl?.let {
            Text(text = "Uploaded sample URL: $it")
        }

        Button(onClick = viewModel::cloneVoiceSample, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Clone Voice")
        }

        clonedVoiceUrl?.let {
            Text(text = "Cloned voice URL: $it")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = viewModel::playClonedVoice, modifier = Modifier.weight(1f)) {
                    Text(text = "Play")
                }
                Button(onClick = viewModel::stopPlayback, modifier = Modifier.weight(1f)) {
                    Text(text = "Stop")
                }
            }
        }

        statusMessage?.let {
            Text(text = it)
        }

        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Text(text = "Loading...", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
