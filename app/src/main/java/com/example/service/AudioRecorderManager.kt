package com.example.service

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * Manages device microphone audio recording using MediaRecorder.
 * Tracks live microphone audio amplitude and recording duration for UI feedback.
 */
class AudioRecorderManager(private val context: Context) {

    private val tag = "AudioRecorderManager"

    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var pollingJob: Job? = null
    private var timerJob: Job? = null

    /**
     * Starts recording audio from the microphone to a local cache file.
     * @return true if recording started successfully, false otherwise.
     */
    fun startRecording(): Boolean {
        if (_isRecording.value) {
            return true
        }

        try {
            val cacheDir = context.cacheDir
            val audioFile = File(cacheDir, "innova_voice_${System.currentTimeMillis()}.m4a")
            currentOutputFile = audioFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            _isRecording.value = true
            _durationSeconds.value = 0
            _error.value = null

            startAmplitudePolling()
            startTimer()
            Log.d(tag, "Started audio recording to: ${audioFile.absolutePath}")
            return true
        } catch (e: IOException) {
            Log.e(tag, "Failed to start recording: ${e.message}", e)
            _error.value = "Microphone error: ${e.localizedMessage}"
            stopAndClean()
            return false
        } catch (e: Exception) {
            Log.e(tag, "Unexpected recording error: ${e.message}", e)
            _error.value = e.localizedMessage ?: "Recording failed"
            stopAndClean()
            return false
        }
    }

    /**
     * Stops the active audio recording and returns the recorded audio file.
     */
    fun stopRecording(): File? {
        if (!_isRecording.value && mediaRecorder == null) {
            return currentOutputFile
        }

        stopJobs()
        val file = currentOutputFile

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.w(tag, "Exception stopping MediaRecorder: ${e.message}")
        } finally {
            mediaRecorder = null
            _isRecording.value = false
            _amplitude.value = 0f
        }

        return file
    }

    /**
     * Cancels recording and deletes the temporary audio file.
     */
    fun cancelRecording() {
        stopJobs()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.w(tag, "Exception canceling MediaRecorder: ${e.message}")
        } finally {
            mediaRecorder = null
            _isRecording.value = false
            _amplitude.value = 0f
            _durationSeconds.value = 0
            currentOutputFile?.delete()
            currentOutputFile = null
        }
    }

    private fun startAmplitudePolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive && _isRecording.value) {
                val maxAmp = try {
                    mediaRecorder?.maxAmplitude ?: 0
                } catch (e: Exception) {
                    0
                }
                // Normalize 0..32767 to 0.0..1.0
                val normalized = (maxAmp / 32767f).coerceIn(0f, 1f)
                _amplitude.value = normalized
                delay(80)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            var elapsed = 0
            while (isActive && _isRecording.value) {
                delay(1000)
                elapsed++
                _durationSeconds.value = elapsed
            }
        }
    }

    private fun stopJobs() {
        pollingJob?.cancel()
        pollingJob = null
        timerJob?.cancel()
        timerJob = null
    }

    private fun stopAndClean() {
        stopJobs()
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null
        _isRecording.value = false
        _amplitude.value = 0f
    }
}
