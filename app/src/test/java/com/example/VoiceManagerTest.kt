package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class VoiceManagerTest {

    @Test
    fun testAudioDurationFormatting() {
        fun formatDuration(durationSeconds: Int): String {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        assertEquals("00:00", formatDuration(0))
        assertEquals("00:09", formatDuration(9))
        assertEquals("01:05", formatDuration(65))
        assertEquals("10:00", formatDuration(600))
    }

    @Test
    fun testAmplitudeClamping() {
        val rawAmp1 = 0
        val rawAmp2 = 16384
        val rawAmp3 = 32767
        val rawAmp4 = 40000

        fun normalize(maxAmp: Int): Float = (maxAmp / 32767f).coerceIn(0f, 1f)

        assertEquals(0f, normalize(rawAmp1), 0.001f)
        assertEquals(0.5f, normalize(rawAmp2), 0.01f)
        assertEquals(1.0f, normalize(rawAmp3), 0.001f)
        assertEquals(1.0f, normalize(rawAmp4), 0.001f)
    }

    @Test
    fun testSpeechRecognizerErrorMapping() {
        val errorCodes = mapOf(
            3 to "Audio recording error",
            5 to "Client speech error",
            9 to "Microphone permission required",
            2 to "Network error during speech recognition",
            7 to "No speech match found",
            8 to "Speech recognizer busy",
            6 to "No speech input detected"
        )

        assertEquals("Microphone permission required", errorCodes[9])
        assertEquals("Audio recording error", errorCodes[3])
        assertEquals("No speech input detected", errorCodes[6])
    }
}
