package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.InnovaGlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay

/**
 * Three animated bouncing dots providing smooth visual typing rhythm.
 */
@Composable
fun TypingDots(
    modifier: Modifier = Modifier,
    dotSize: Dp = 7.dp,
    dotColors: List<Color> = listOf(NeonCyan, Color(0xFF818CF8), ElectricPurple),
    bounceHeight: Dp = 6.dp,
    spacing: Dp = 5.dp
) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 160L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Row(
        modifier = modifier.testTag("typing_dots_row"),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEachIndexed { index, animatable ->
            val value = animatable.value
            val color = dotColors.getOrElse(index) { NeonCyan }

            Box(
                modifier = Modifier
                    .offset(y = (-bounceHeight * value))
                    .size(dotSize)
                    .scale(0.85f + 0.3f * value)
                    .alpha(0.4f + 0.6f * value)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/**
 * Blinking cursor indicating active character-by-character generation in streaming messages.
 */
@Composable
fun BlinkingCursor(
    modifier: Modifier = Modifier,
    cursorColor: Color = NeonCyan,
    width: Dp = 2.5.dp,
    height: Dp = 15.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blinkingCursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .alpha(alpha)
            .clip(RoundedCornerShape(1.dp))
            .background(cursorColor)
            .testTag("streaming_blinking_cursor")
    )
}

/**
 * Full AI Chat Message Bubble containing animated typing dots and dynamic status feedback
 * while the model processes the request before tokens arrive.
 */
@Composable
fun TypingIndicatorBubble(
    modifier: Modifier = Modifier,
    modelName: String = "Gemini 3.5 Flash"
) {
    val thinkingPhrases = remember {
        listOf(
            "Thinking...",
            "Analyzing prompt...",
            "Connecting thoughts...",
            "Drafting response...",
            "Synthesizing knowledge..."
        )
    }

    var phraseIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2400L)
            phraseIndex = (phraseIndex + 1) % thinkingPhrases.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bubbleAura")
    val pulseBorderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseBorder"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("typing_indicator_bubble"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        InnovaLogoBadge(size = 32.dp, showGlow = true)

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(CyberDarkSurfaceElevated)
                .border(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = pulseBorderAlpha * 0.5f),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TypingDots(
                        dotSize = 7.dp,
                        bounceHeight = 6.dp,
                        spacing = 5.dp
                    )

                    Text(
                        text = thinkingPhrases[phraseIndex],
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.testTag("typing_indicator_status_text")
                    )
                }

                Text(
                    text = "$modelName is preparing your answer",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondaryDark,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

/**
 * Animated glowing gradient bar running beneath the TopAppBar during active AI generation.
 */
@Composable
fun GlowingStreamProgressLine(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streamLine")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lineProgress"
    )

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            ElectricPurple.copy(alpha = 0.2f),
            NeonCyan,
            BentoIndigo,
            NeonCyan,
            ElectricPurple.copy(alpha = 0.2f)
        ),
        startX = offsetProgress * 600f,
        endX = (offsetProgress * 600f) + 400f
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.5.dp)
            .background(gradient)
            .testTag("glowing_stream_progress_line")
    )
}
