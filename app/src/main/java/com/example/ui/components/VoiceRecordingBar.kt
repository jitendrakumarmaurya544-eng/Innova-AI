package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.util.Locale

/**
 * Animated voice recording control bar displayed while microphone audio is being captured.
 * Shows real-time audio amplitude waveform, elapsed recording duration, live partial transcription,
 * and quick cancel/accept controls.
 */
@Composable
fun VoiceRecordingBar(
    isRecording: Boolean,
    durationSeconds: Int,
    amplitude: Float,
    partialText: String,
    onCancel: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isRecording,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFE11D48).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = CyberDarkSurfaceElevated,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Row: Recording status, timer, audio wave, and action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Pulsing Red Recording Indicator & Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PulsingRecordingDot()

                        val formattedTime = remember(durationSeconds) {
                            val minutes = durationSeconds / 60
                            val seconds = durationSeconds % 60
                            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                        }

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFFB7185),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // Center: Real-time Audio Waveform
                    ReactiveAudioWaveform(
                        amplitude = amplitude,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Right: Discard (Cancel) & Apply (Check) Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A1C28))
                                .testTag("btn_voice_cancel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel recording",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onFinish,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F3830))
                                .testTag("btn_voice_finish")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Accept speech transcription",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom Row: Live Transcription Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (partialText.isNotBlank()) {
                            "\"$partialText\""
                        } else {
                            "Listening... speak into your microphone"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (partialText.isNotBlank()) TextPrimaryDark else TextSecondaryDark,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Pulsing red indicator dot showing active recording state.
 */
@Composable
private fun PulsingRecordingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_dot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFFE11D48))
    )
}

/**
 * Reactive multi-bar audio visualizer whose height responds dynamically to audio amplitude.
 */
@Composable
private fun ReactiveAudioWaveform(
    amplitude: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 9
) {
    val clampedAmp = amplitude.coerceIn(0.1f, 1.0f)

    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val multiplier = listOf(0.4f, 0.7f, 1.0f, 0.85f, 1.2f, 0.9f, 1.1f, 0.6f, 0.5f)
        for (i in 0 until barCount) {
            val factor = multiplier[i % multiplier.size]
            val barHeight = (clampedAmp * factor * 22f).coerceIn(4f, 22f)

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        if (i % 2 == 0) NeonCyan else Color(0xFFE11D48)
                    )
            )
        }
    }
}
