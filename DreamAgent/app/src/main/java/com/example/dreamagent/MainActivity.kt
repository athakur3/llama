package com.example.dreamagent

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dreamagent.ui.theme.DreamAgentTheme
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private lateinit var audioFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SocketManager.getSocket()

        setContent {
            DreamAgentTheme {
                AudioRecordScreen(
                    onRecordAudio = { toggleRecording() },
                    onPlayAudio = { togglePlayback() },
                    isRecording = isRecording,
                    isPlaying = isPlaying
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SocketManager.reconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.disconnect()
        releaseMediaRecorder()
        releaseMediaPlayer()
    }

    private fun toggleRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        } else {
            if (!isRecording) startRecording() else stopRecording()
        }
    }

    private fun startRecording() {
        println("REC")
        try {
            releaseMediaRecorder()
            audioFile = File(externalCacheDir?.absolutePath, "audio_record.mp4") // Using MPEG_4
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // MPEG_4 format
                setOutputFile(audioFile.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // AAC encoding
                setAudioChannels(1)
                setAudioSamplingRate(44100)
                prepare()
                start()
            }
            isRecording = true
        } catch (e: IOException) {
            Log.e("MainActivity", "prepare() failed")
        }
    }

    private fun stopRecording() {
        println("STOP REC")
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false

        val base64Audio = convertAudioToBase64(audioFile)
        SocketManager.getSocket().emit("audio_data", audioFile.absolutePath)
        Log.d("SocketIO", "Emitted 'audio_ready' event with file path: ${audioFile.absolutePath}")
    }

    private fun togglePlayback() {
        if (!isPlaying) startPlaying() else stopPlaying()
    }

    private fun startPlaying() {

        println("START PLAY")
        try {
            releaseMediaPlayer()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                print(audioFile.absolutePath)
                prepare()
                start()
            }
            isPlaying = true
            mediaPlayer?.setOnCompletionListener {
                stopPlaying()
            }
        } catch (e: IOException) {
            Log.e("MainActivity", "prepare() failed")
        }
    }

    private fun stopPlaying() {
        println("STOP PLAY")
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    private fun convertAudioToBase64(file: File): String {
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}

@Composable
fun AudioRecordScreen(
    onRecordAudio: () -> Unit,
    onPlayAudio: () -> Unit,
    isRecording: Boolean,
    isPlaying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRecordAudio) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPlayAudio, enabled = !isRecording) {
            Text(text = if (isPlaying) "Stop Playing" else "Start Playing")
        }
    }
}
