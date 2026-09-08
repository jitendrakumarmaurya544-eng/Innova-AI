package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Bento Grid Dark Obsidian & Indigo Palette
val BentoDarkBg = Color(0xFF05050A)
val BentoCardSurface = Color(0xFF1A1A2E)
val BentoDeepSurface = Color(0xFF131320)
val BentoSurfaceElevated = Color(0xFF25253D)
val BentoBorderSubtle = Color(0x14FFFFFF) // 8% white
val BentoBorderHighlight = Color(0x28FFFFFF) // 16% white
val BentoBorderGlow = Color(0x556366F1) // 33% indigo glow

// Core Bento Accents
val BentoIndigo = Color(0xFF6366F1)
val BentoViolet = Color(0xFF8B5CF6)
val BentoPurple = Color(0xFFA855F7)
val BentoEmerald = Color(0xFF34D399)
val BentoRose = Color(0xFFFB7185)
val BentoAmber = Color(0xFFFBBF24)
val BentoSky = Color(0xFF38BDF8)

// Text tokens
val BentoTextPrimary = Color(0xFFF0F0F5)
val BentoTextSlate300 = Color(0xFFCBD5E1)
val BentoTextSlate400 = Color(0xFF94A3B8)
val BentoTextSlate500 = Color(0xFF64748B)

// Compatibility aliases mapping to Bento Theme
val CyberDarkBg = BentoDarkBg
val CyberDarkSurface = BentoCardSurface
val CyberDarkSurfaceElevated = BentoSurfaceElevated
val CyberCardBorder = BentoBorderSubtle

val ElectricPurple = BentoIndigo
val VividViolet = BentoViolet
val NeonCyan = BentoIndigo
val BrightBlue = BentoSky
val CyberAzure = BentoSky
val CyberMagenta = BentoPurple
val CyberEmerald = BentoEmerald
val CyberAmber = BentoAmber

val TextPrimaryDark = BentoTextPrimary
val TextSecondaryDark = BentoTextSlate400
val TextTertiaryDark = BentoTextSlate500

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)

val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)

// Bento Gradients
val BentoHeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
)

val BentoLogoGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
)

val InnovaPrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFA855F7))
)

val InnovaPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
)

val InnovaCyanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF38BDF8))
)

val InnovaCardGradient = Brush.linearGradient(
    colors = listOf(Color(0x1F6366F1), Color(0x0F8B5CF6))
)

val InnovaGlassBorder = Brush.linearGradient(
    colors = listOf(Color(0x406366F1), Color(0x20A855F7), Color(0x18FFFFFF))
)

val InnovaVoiceGlow = Brush.radialGradient(
    colors = listOf(Color(0x666366F1), Color(0x338B5CF6), Color(0x00000000))
)

