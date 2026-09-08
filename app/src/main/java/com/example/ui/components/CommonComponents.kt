package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoLogoGradient
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSlate300
import com.example.ui.theme.BentoTextSlate400
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.InnovaGlassBorder
import com.example.ui.theme.InnovaPrimaryGradient
import com.example.ui.theme.NeonCyan

@Composable
fun InnovaLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (showGlow) pulseScale else 1f)
            .shadow(if (showGlow) 14.dp else 0.dp, RoundedCornerShape(size * 0.28f), spotColor = BentoIndigo)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(BentoLogoGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "i",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.52f).sp
        )
    }
}

@Composable
fun InnovaCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderBrush: Brush = Brush.linearGradient(listOf(BentoBorderSubtle, BentoBorderSubtle)),
    backgroundColor: Color = BentoCardSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, borderBrush, RoundedCornerShape(cornerRadius))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        content()
    }
}

@Composable
fun BentoPulseSuggestion(
    suggestionText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x14FFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BentoEmerald.copy(alpha = alphaAnim))
                    .shadow(6.dp, CircleShape, spotColor = BentoEmerald)
            )

            Text(
                text = "Suggested: \"$suggestionText\"",
                color = BentoTextSlate300,
                fontSize = 12.sp,
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isWaving: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    barColor: Color = BentoIndigo
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave3"
    )

    Row(
        modifier = modifier.height(30.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = listOf(wave1, wave2, wave3, wave1 * 0.7f, wave2 * 0.9f)
        for (i in 0 until barCount) {
            val h = if (isWaving) heights[i % heights.size] else 4f
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isWaving) barColor else barColor.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val (icon, color) = getCategoryIconAndColor(category)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}

fun getCategoryIconAndColor(category: String): Pair<ImageVector, Color> {
    return when (category.lowercase()) {
        "chat", "ask ai" -> Pair(Icons.Default.AutoAwesome, BentoIndigo)
        "write", "create" -> Pair(Icons.Default.Create, Color(0xFFFF70A6))
        "code", "coding" -> Pair(Icons.Default.Code, Color(0xFF00F0A8))
        "image", "images" -> Pair(Icons.Default.Image, Color(0xFFFFB703))
        "study", "learn" -> Pair(Icons.Default.School, BentoViolet)
        "translate" -> Pair(Icons.Default.Translate, Color(0xFF38BDF8))
        "summarize", "document" -> Pair(Icons.Default.Summarize, Color(0xFF34D399))
        "brainstorm", "ideas" -> Pair(Icons.Default.Lightbulb, Color(0xFFFBBF24))
        else -> Pair(Icons.Default.Psychology, BentoIndigo)
    }
}

