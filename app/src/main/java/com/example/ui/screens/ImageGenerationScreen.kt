package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.SavedCreationEntity
import com.example.ui.components.InnovaCard
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBorderGlow
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BentoDarkBg
import com.example.ui.theme.BentoDeepSurface
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoLogoGradient
import com.example.ui.theme.BentoRose
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSlate300
import com.example.ui.theme.BentoTextSlate400
import com.example.ui.theme.BentoTextSlate500
import com.example.ui.theme.BentoViolet
import com.example.viewmodel.InnovaViewModel

@Composable
fun ImageGenerationScreen(
    viewModel: InnovaViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prompt by viewModel.imagePrompt.collectAsState()
    val style by viewModel.imageStyle.collectAsState()
    val ratio by viewModel.imageAspectRatio.collectAsState()
    val selectedModel by viewModel.selectedImageModel.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()
    val isEnhancing by viewModel.isEnhancingPrompt.collectAsState()
    val statusText by viewModel.imageGenerationStatus.collectAsState()
    val imageBase64 by viewModel.generatedImageBase64.collectAsState()
    val imageDesc by viewModel.generatedImageDesc.collectAsState()
    val fullscreenBase64 by viewModel.fullscreenImageBase64.collectAsState()
    val savedCreations by viewModel.savedCreations.collectAsState()

    val imageHistory = remember(savedCreations) {
        savedCreations.filter { it.type == "image" && !it.imageBase64.isNullOrBlank() }
    }

    val styles = listOf(
        "Futuristic Neon",
        "Cyberpunk",
        "Cinematic 3D",
        "Photorealistic",
        "Anime & Manga",
        "Digital Concept",
        "Watercolor Fantasy",
        "Retro Synthwave",
        "Sci-Fi Minimalist"
    )

    val aspectRatios = listOf(
        Pair("1:1", "Square"),
        Pair("16:9", "Landscape"),
        Pair("9:16", "Portrait"),
        Pair("4:3", "Classic")
    )

    val models = listOf(
        Pair("gemini-2.5-flash-image", "Gemini 2.5 Flash"),
        Pair("gemini-3.1-flash-image-preview", "Gemini 3.1 HQ")
    )

    val creativePresets = listOf(
        "A neon cybernetic snow leopard perched on a Tokyo skyscraper in rain, hyperrealistic 8k",
        "An astronaut meditating inside an ancient bioluminescent crystalline cavern on Mars",
        "A steampunk mechanical clockwork owl with brass feathers and glowing sapphire eyes",
        "A tranquil zen floating island in the clouds with bonsai trees and miniature waterfalls",
        "A retro-futuristic synthwave sports car speeding toward a digital sunset wireframe horizon",
        "A surrealistic cosmic library where ancient books fly through stellar nebulae"
    )

    var presetIndex by remember { mutableIntStateOf(0) }
    var showModelPicker by remember { mutableStateOf(false) }

    // Decode generated bitmap
    val currentBitmap: Bitmap? = remember(imageBase64) {
        if (imageBase64.isNullOrBlank()) null
        else {
            try {
                val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: Exception) {
                null
            }
        }
    }

    // Fullscreen Dialog
    if (fullscreenBase64 != null) {
        val fullscreenBitmap = remember(fullscreenBase64) {
            try {
                val bytes = Base64.decode(fullscreenBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: Exception) {
                null
            }
        }

        Dialog(
            onDismissRequest = { viewModel.setFullscreenImage(null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.94f))
                    .clickable { viewModel.setFullscreenImage(null) },
                contentAlignment = Alignment.Center
            ) {
                if (fullscreenBitmap != null) {
                    Image(
                        bitmap = fullscreenBitmap.asImageBitmap(),
                        contentDescription = "Fullscreen Artwork",
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visual Preview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    IconButton(
                        onClick = { viewModel.setFullscreenImage(null) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x55000000), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoDeepSurface)
                                .border(1.dp, BentoBorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BentoTextPrimary
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Image Generation",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary,
                                    fontSize = 22.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BentoIndigo.copy(alpha = 0.2f))
                                    .border(1.dp, BentoIndigo.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AI Studio",
                                    color = BentoIndigo,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Text(
                            text = "Type a description to render high-fidelity visuals",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextSlate400,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Model Selector Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoDeepSurface)
                        .border(1.dp, BentoBorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { showModelPicker = !showModelPicker }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BentoEmerald)
                        )
                        Text(
                            text = if (selectedModel.contains("3.1")) "3.1 Pro" else "2.5 Flash",
                            color = BentoTextSlate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Model Settings",
                            tint = BentoTextSlate400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Model Picker Drawer / Dropdown
        if (showModelPicker) {
            item {
                InnovaCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BentoDeepSurface
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select Image Generation Engine",
                            color = BentoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            models.forEach { (modelKey, modelLabel) ->
                                val isSelected = modelKey == selectedModel
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BentoIndigo.copy(alpha = 0.25f) else BentoCardSurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) BentoIndigo else BentoBorderSubtle,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            viewModel.setSelectedImageModel(modelKey)
                                            showModelPicker = false
                                        }
                                        .padding(vertical = 10.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = modelLabel,
                                            color = if (isSelected) Color.White else BentoTextSlate300,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = if (modelKey.contains("2.5")) "Fast • 1K Res" else "HQ • 2K Ultra",
                                            color = if (isSelected) BentoIndigo else BentoTextSlate500,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Prompt Input Card
        item {
            InnovaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Visual Prompt Description",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = BentoTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Surprise Me Preset Prompt
                            TextButton(
                                onClick = {
                                    val randomPreset = creativePresets[presetIndex % creativePresets.size]
                                    viewModel.applyPresetPrompt(randomPreset)
                                    presetIndex++
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Shuffle,
                                    contentDescription = null,
                                    tint = BentoAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Surprise Me",
                                    color = BentoAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (prompt.isNotBlank()) {
                                IconButton(
                                    onClick = { viewModel.clearImagePrompt() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear Prompt",
                                        tint = BentoTextSlate400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Prompt text area
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.setImagePrompt(it) },
                        placeholder = {
                            Text(
                                text = "Describe what you want to see, e.g., 'A cyberpunk cybernetic tiger roaming a neon rain-slicked Tokyo street, hyperrealistic 8k...'",
                                color = BentoTextSlate500,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .testTag("image_prompt_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoDeepSurface,
                            unfocusedContainerColor = BentoDeepSurface,
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorderSubtle,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        )
                    )

                    // Action row: AI Enhance Prompt + Quick Inspiration Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.enhanceImagePrompt() },
                            enabled = !isEnhancing && prompt.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoDeepSurface,
                                contentColor = BentoViolet
                            ),
                            modifier = Modifier
                                .border(1.dp, BentoViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .height(38.dp)
                                .testTag("enhance_prompt_button")
                        ) {
                            if (isEnhancing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = BentoViolet,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BentoViolet,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnhancing) "Refining..." else "Enhance Prompt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "${prompt.length} chars",
                            color = BentoTextSlate500,
                            fontSize = 11.sp
                        )
                    }

                    // Preset prompt chips
                    Text(
                        text = "Quick Inspirations:",
                        color = BentoTextSlate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tags = listOf(
                            "Cyberpunk Alley",
                            "Bioluminescent Forest",
                            "Cosmic Astronaut",
                            "Solarpunk Garden",
                            "Vintage Mecha",
                            "Origami Phoenix"
                        )
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BentoDeepSurface)
                                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val generatedPrompt = when (tag) {
                                            "Cyberpunk Alley" -> "A rainy cyberpunk alley illuminated by flickering holographic signage and neon reflections"
                                            "Bioluminescent Forest" -> "A mystical bioluminescent forest with glowing azure mushrooms, weeping willow trees, and fireflies"
                                            "Cosmic Astronaut" -> "An astronaut gazing at a shattered spiral galaxy from the rim of a massive obsidian space station"
                                            "Solarpunk Garden" -> "A solarpunk rooftop botanical garden integrated with futuristic glass windmills and solar petals"
                                            "Vintage Mecha" -> "A weathered dieselpunk mechanical giant standing peacefully in a field of sunflowers"
                                            else -> "An origami phoenix reborn from iridescent embers and golden light ribbons"
                                        }
                                        viewModel.applyPresetPrompt(generatedPrompt)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = BentoTextSlate300,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Style & Aspect Ratio Selection Card
        item {
            InnovaCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = BentoCardSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Visual Style Carousel
                    Text(
                        text = "Artistic Style",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = BentoTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(styles) { s ->
                            val isSelected = s == style
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) BentoLogoGradient
                                        else Brush.linearGradient(listOf(BentoDeepSurface, BentoDeepSurface))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) BentoBorderGlow else BentoBorderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setImageStyle(s) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = s,
                                    color = if (isSelected) Color.White else BentoTextSlate300,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Aspect Ratio Selector
                    Text(
                        text = "Aspect Ratio",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = BentoTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        aspectRatios.forEach { (ratioKey, ratioLabel) ->
                            val isSelected = ratioKey == ratio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) BentoIndigo.copy(alpha = 0.3f)
                                        else BentoDeepSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) BentoIndigo else BentoBorderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setImageAspectRatio(ratioKey) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = ratioKey,
                                        color = if (isSelected) BentoIndigo else BentoTextSlate300,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = ratioLabel,
                                        color = if (isSelected) BentoTextPrimary else BentoTextSlate500,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // 4. Primary Generate Button
                    Button(
                        onClick = { viewModel.generateImage() },
                        enabled = !isGenerating && prompt.isNotBlank(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = BentoDeepSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = if (!isGenerating && prompt.isNotBlank()) 10.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = BentoIndigo
                            )
                            .background(
                                if (!isGenerating && prompt.isNotBlank()) BentoLogoGradient
                                else Brush.linearGradient(listOf(BentoDeepSurface, BentoDeepSurface)),
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("generate_image_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Synthesizing Visual...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Generate Visual",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Active Generation Animation & Progress State
        if (isGenerating) {
            item {
                InnovaCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BentoCardSurface
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulsePreview")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 0.96f,
                        targetValue = 1.02f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(BentoIndigo.copy(alpha = 0.2f))
                                .border(2.dp, BentoIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = statusText,
                                color = BentoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rendering style '$style' with Gemini Image API",
                                color = BentoTextSlate400,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 6. Rendered Visual Showcase Card
        if (currentBitmap != null) {
            item {
                InnovaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    backgroundColor = BentoCardSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title row & action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Generated Visual",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = BentoTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "$style • $ratio",
                                    color = BentoIndigo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Fullscreen inspect
                                IconButton(
                                    onClick = { viewModel.setFullscreenImage(imageBase64) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BentoDeepSurface)
                                ) {
                                    Icon(
                                        Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = BentoTextSlate300,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Copy prompt
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Innova Art Prompt", prompt))
                                        Toast.makeText(context, "Prompt copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BentoDeepSurface)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Prompt",
                                        tint = BentoTextSlate300,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Share visual
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "🎨 Created with Innova AI Visual Studio:\n\"$prompt\"\nStyle: $style"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Artwork"))
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BentoDeepSurface)
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Share Artwork",
                                        tint = BentoTextSlate300,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Re-roll / regenerate
                                IconButton(
                                    onClick = { viewModel.generateImage() },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BentoDeepSurface)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = BentoAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Artwork Frame
                        val imageAspectRatioFloat = when (ratio) {
                            "16:9" -> 16f / 9f
                            "9:16" -> 9f / 16f
                            "4:3" -> 4f / 3f
                            else -> 1f
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(imageAspectRatioFloat)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, BentoBorderGlow, RoundedCornerShape(18.dp))
                                .clickable { viewModel.setFullscreenImage(imageBase64) }
                        ) {
                            Image(
                                bitmap = currentBitmap.asImageBitmap(),
                                contentDescription = "Generated artwork for $prompt",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Tap to view full label
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Fullscreen,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Tap for Full Size",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Description / Context note
                        if (!imageDesc.isNullOrBlank()) {
                            Text(
                                text = imageDesc!!,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextSlate400,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }

                        // Save confirmation chip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BentoEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Saved to Gallery automatically",
                                    color = BentoEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            TextButton(
                                onClick = {
                                    Toast.makeText(context, "Artwork safely stored in your local gallery", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(
                                    text = "View in History",
                                    color = BentoIndigo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Recent Creations Gallery
        if (imageHistory.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Your Visual Gallery (${imageHistory.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(imageHistory) { item ->
                            val historyBitmap = remember(item.imageBase64) {
                                try {
                                    val bytes = Base64.decode(item.imageBase64, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BentoCardSurface)
                                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.loadCreationIntoViewer(item)
                                        Toast.makeText(context, "Loaded \"${item.title}\"", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Column {
                                    if (historyBitmap != null) {
                                        Image(
                                            bitmap = historyBitmap.asImageBitmap(),
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .background(BentoDeepSurface),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Image,
                                                null,
                                                tint = BentoTextSlate500,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = item.title,
                                            color = BentoTextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.metadata.ifBlank { "Artwork" },
                                            color = BentoTextSlate400,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
