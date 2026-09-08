package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ConversationEntity
import com.example.ui.components.BentoPulseSuggestion
import com.example.ui.components.CategoryBadge
import com.example.ui.components.InnovaCard
import com.example.ui.components.InnovaLogoBadge
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBorderHighlight
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BentoDarkBg
import com.example.ui.theme.BentoDeepSurface
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoHeroGradient
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoLogoGradient
import com.example.ui.theme.BentoRose
import com.example.ui.theme.BentoSky
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSlate300
import com.example.ui.theme.BentoTextSlate400
import com.example.ui.theme.BentoTextSlate500
import com.example.ui.theme.BentoViolet
import com.example.viewmodel.AppTab
import com.example.viewmodel.InnovaViewModel
import com.example.viewmodel.StudioTab
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val welcomeMessage by viewModel.welcomeMessage.collectAsState()
    val isWelcomeMessageVisible by viewModel.isWelcomeMessageVisible.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isGeneratingWelcome by viewModel.isGeneratingWelcome.collectAsState()
    var inputPrompt by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var suggestionIndex by remember { mutableIntStateOf(0) }

    val filteredConversations = remember(searchQuery, conversations) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val query = searchQuery.trim().lowercase()
            conversations.filter { conv ->
                conv.title.lowercase().contains(query) ||
                conv.previewSnippet.lowercase().contains(query) ||
                conv.category.lowercase().contains(query)
            }
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val suggestedPrompts = remember {
        listOf(
            "Explain quantum computing with a simple analogy",
            "Write a Python script to scrape and summarize tech news",
            "Generate 5 disruptive startup ideas for AI in healthcare",
            "Draft a compelling pitch email for venture investors",
            "Create a flashcard study guide for machine learning algorithms",
            "Design a modern microservice architecture in Kotlin"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InnovaLogoBadge(size = 38.dp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Innova",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoIndigo,
                                    fontSize = 18.sp
                                )
                            )
                        }
                        Text(
                            text = "Bento AI Workspace",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextSlate500,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Model status selector
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x14FFFFFF))
                        .border(1.dp, BentoBorderSubtle, RoundedCornerShape(20.dp))
                        .clickable {
                            val nextModel = if (selectedModel == "gemini-3.5-flash") "gemini-3.1-pro-preview" else "gemini-3.5-flash"
                            viewModel.selectModel(nextModel)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (selectedModel.contains("pro")) BentoAmber else BentoIndigo)
                        )
                        Text(
                            text = if (selectedModel.contains("pro")) "Gemini 3.1 Pro" else "Gemini 3.5 Flash",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Top Search Bar for quickly finding past conversations or topics
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BentoCardSurface)
                    .border(
                        1.dp,
                        if (searchQuery.isNotEmpty()) BentoIndigo.copy(alpha = 0.6f) else BentoBorderSubtle,
                        RoundedCornerShape(20.dp)
                    )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search past conversations & topics...",
                            color = BentoTextSlate500,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search history",
                            tint = if (searchQuery.isNotEmpty()) BentoIndigo else BentoTextSlate400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = BentoTextSlate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary,
                        cursorColor = BentoIndigo
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar")
                )
            }
        }

        // Trending Topics & Common Task Chips
        item {
            val taskChips = remember {
                listOf(
                    TrendingTaskChip(
                        label = "Brainstorm ideas",
                        icon = Icons.Default.Lightbulb,
                        color = BentoAmber,
                        tag = "chip_brainstorm",
                        action = { viewModel.setStudioTab(StudioTab.BRAINSTORM) }
                    ),
                    TrendingTaskChip(
                        label = "Debug code",
                        icon = Icons.Default.Code,
                        color = BentoEmerald,
                        tag = "chip_debug_code",
                        action = { viewModel.setStudioTab(StudioTab.CODE) }
                    ),
                    TrendingTaskChip(
                        label = "Write content",
                        icon = Icons.Default.Create,
                        color = BentoRose,
                        tag = "chip_write_content",
                        action = { viewModel.setStudioTab(StudioTab.WRITER) }
                    ),
                    TrendingTaskChip(
                        label = "Generate art",
                        icon = Icons.Default.Image,
                        color = BentoViolet,
                        tag = "chip_generate_art",
                        action = { viewModel.setStudioTab(StudioTab.IMAGES) }
                    ),
                    TrendingTaskChip(
                        label = "Summarize notes",
                        icon = Icons.Default.Summarize,
                        color = BentoSky,
                        tag = "chip_summarize",
                        action = { viewModel.setStudioTab(StudioTab.SUMMARIZE) }
                    ),
                    TrendingTaskChip(
                        label = "Translate text",
                        icon = Icons.Default.Translate,
                        color = BentoIndigo,
                        tag = "chip_translate",
                        action = { viewModel.setStudioTab(StudioTab.TRANSLATE) }
                    ),
                    TrendingTaskChip(
                        label = "Study & quiz",
                        icon = Icons.Default.School,
                        color = BentoViolet,
                        tag = "chip_study_quiz",
                        action = { viewModel.setStudioTab(StudioTab.STUDY) }
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                taskChips.forEach { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BentoCardSurface)
                            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(16.dp))
                            .clickable { chip.action() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag(chip.tag)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(chip.color.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = chip.icon,
                                    contentDescription = null,
                                    tint = chip.color,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = chip.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Search Results Section (Shown dynamically when search is active)
        if (searchQuery.isNotBlank()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search Results (${filteredConversations.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoIndigo,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    }

                    if (filteredConversations.isEmpty()) {
                        InnovaCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp,
                            backgroundColor = BentoCardSurface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = BentoTextSlate500,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "No matching conversations found",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = BentoTextSlate400,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    text = "Try searching for another topic, title, or keyword",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BentoTextSlate500,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    } else {
                        filteredConversations.forEach { conv ->
                            RecentConversationBentoItem(
                                conversation = conv,
                                onClick = {
                                    viewModel.openConversation(conv.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // AI Welcome Greeting Speech Banner
        item {
            AnimatedVisibility(
                visible = isWelcomeMessageVisible && !welcomeMessage.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                WelcomeSpeechBanner(
                    message = welcomeMessage ?: "",
                    isSpeaking = isSpeaking,
                    isGenerating = isGeneratingWelcome,
                    persona = userProfile.persona,
                    userName = userProfile.name,
                    onReplay = { viewModel.replayWelcomeMessage() },
                    onStop = { viewModel.voiceManager.stopSpeaking() },
                    onDismiss = { viewModel.dismissWelcomeMessage() }
                )
            }
        }

        // Title Section
        item {
            Column(
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$greeting, ${userProfile.name.split(" ").firstOrNull() ?: "Explorer"}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        fontSize = 26.sp
                    )
                )
                Text(
                    text = "“Your Ideas, Powered by AI.”",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BentoTextSlate400,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }

        // Bento Grid (Row 1: Hero Large Ask Box + 2 Right Squares)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large Bento Hero Tile
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(180.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(BentoHeroGradient)
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = BentoIndigo)
                        .clickable {
                            viewModel.startNewConversation("Chat")
                        }
                        .padding(18.dp)
                        .testTag("bento_hero_chat")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Frosted Icon Badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Ask Innova\nAnything",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp
                                )
                            )
                            Text(
                                text = "Start a new conversation",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Right 2 Square Bento Tiles (Write & Code)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Write Bento Tile
                    BentoSquareTile(
                        title = "WRITE",
                        icon = Icons.Default.Create,
                        iconTint = BentoRose,
                        modifier = Modifier.weight(1f),
                        testTag = "bento_write",
                        onClick = { viewModel.setStudioTab(StudioTab.WRITER) }
                    )

                    // Code Bento Tile
                    BentoSquareTile(
                        title = "CODE",
                        icon = Icons.Default.Code,
                        iconTint = BentoEmerald,
                        modifier = Modifier.weight(1f),
                        testTag = "bento_code",
                        onClick = { viewModel.setStudioTab(StudioTab.CODE) }
                    )
                }
            }
        }

        // Bento Grid (Row 2: Generate & Study 2-Col Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Generate Bento Card
                BentoRectCard(
                    title = "Generate",
                    subtitle = "Text to Image",
                    icon = Icons.Default.Image,
                    iconTint = BentoAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "bento_generate",
                    onClick = { viewModel.setStudioTab(StudioTab.IMAGES) }
                )

                // Study Bento Card
                BentoRectCard(
                    title = "Study",
                    subtitle = "Learning Tools",
                    icon = Icons.Default.School,
                    iconTint = BentoViolet,
                    modifier = Modifier.weight(1f),
                    testTag = "bento_study",
                    onClick = { viewModel.setStudioTab(StudioTab.STUDY) }
                )
            }
        }

        // Bento Grid (Row 3: Summarize Documents Wide Card)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(BentoCardSurface)
                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(26.dp))
                    .clickable { viewModel.setStudioTab(StudioTab.SUMMARIZE) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("bento_summarize")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(BentoIndigo.copy(alpha = 0.15f))
                                .border(1.dp, BentoIndigo.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Summarize,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Summarize Documents",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = BentoTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Upload PDF, Doc, or paste URL",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextSlate400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BentoTextSlate300,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Bento Grid (Row 4: Translate & Brainstorm)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoRectCard(
                    title = "Translate",
                    subtitle = "30+ Dialects",
                    icon = Icons.Default.Translate,
                    iconTint = BentoSky,
                    modifier = Modifier.weight(1f),
                    testTag = "bento_translate",
                    onClick = { viewModel.setStudioTab(StudioTab.TRANSLATE) }
                )

                BentoRectCard(
                    title = "Brainstorm",
                    subtitle = "Ideation Hub",
                    icon = Icons.Default.Lightbulb,
                    iconTint = BentoAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "bento_brainstorm",
                    onClick = { viewModel.setStudioTab(StudioTab.BRAINSTORM) }
                )
            }
        }

        // Live Suggested Prompt Pulse Pill
        item {
            BentoPulseSuggestion(
                suggestionText = suggestedPrompts[suggestionIndex % suggestedPrompts.size],
                onClick = {
                    val prompt = suggestedPrompts[suggestionIndex % suggestedPrompts.size]
                    suggestionIndex++
                    viewModel.startNewConversation(category = "Chat", initialPrompt = prompt)
                }
            )
        }

        // Large Chat Input Box (Bento Pill)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(BentoCardSurface)
                    .border(1.dp, BentoBorderSubtle, RoundedCornerShape(32.dp))
                    .padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = {
                            Text(
                                text = "Ask anything...",
                                color = BentoTextSlate500,
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_chat_input"),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            viewModel.voiceManager.startListening { spoken ->
                                inputPrompt = spoken
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("home_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = BentoTextSlate400,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                viewModel.startNewConversation(category = "Chat", initialPrompt = inputPrompt)
                                inputPrompt = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BentoIndigo)
                            .testTag("home_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Suggested Prompts Horizontal Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Suggested Prompts",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        fontSize = 15.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    suggestedPrompts.forEach { prompt ->
                        InnovaCard(
                            modifier = Modifier
                                .width(220.dp)
                                .height(90.dp),
                            cornerRadius = 20.dp,
                            backgroundColor = BentoCardSurface,
                            onClick = {
                                viewModel.startNewConversation(category = "Chat", initialPrompt = prompt)
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = BentoTextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tap to prompt",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BentoIndigo,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = BentoIndigo,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Conversations Bento Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Conversations",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary,
                            fontSize = 15.sp
                        )
                    )

                    if (conversations.isNotEmpty()) {
                        Text(
                            text = "View All (${conversations.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoIndigo,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable { viewModel.setTab(AppTab.HISTORY) }
                        )
                    }
                }

                if (conversations.isEmpty()) {
                    InnovaCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        backgroundColor = BentoCardSurface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = BentoIndigo.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "No conversations yet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = BentoTextSlate400,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "Select any bento studio or type a prompt above to get started!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextSlate500,
                                    fontSize = 12.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    conversations.take(4).forEach { conv ->
                        RecentConversationBentoItem(
                            conversation = conv,
                            onClick = { viewModel.openConversation(conv.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoSquareTile(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BentoCardSurface)
            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BentoTextSlate400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun BentoRectCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BentoCardSurface)
            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BentoTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BentoTextSlate400,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecentConversationBentoItem(
    conversation: ConversationEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(conversation.updatedAt) {
        SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(conversation.updatedAt))
    }

    InnovaCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = BentoCardSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryBadge(category = conversation.category)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = BentoTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (conversation.previewSnippet.isNotBlank()) {
                    Text(
                        text = conversation.previewSnippet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoTextSlate400,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontSize = 10.sp
                )
            )
        }
    }
}

private data class TrendingTaskChip(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val tag: String,
    val action: () -> Unit
)

@Composable
private fun WelcomeSpeechBanner(
    message: String,
    isSpeaking: Boolean,
    isGenerating: Boolean,
    persona: String,
    userName: String,
    onReplay: () -> Unit,
    onStop: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1645),
                        Color(0xFF13102D),
                        Color(0xFF0F0D24)
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        BentoIndigo.copy(alpha = 0.6f),
                        BentoViolet.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("welcome_speech_banner")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSpeaking) BentoViolet.copy(alpha = 0.25f) else BentoIndigo.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                if (isSpeaking) BentoViolet else BentoIndigo.copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = if (isSpeaking) BentoViolet else BentoIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "AI Audio Welcome",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = BentoTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            if (isSpeaking) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BentoViolet.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE SPEECH",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BentoViolet,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Persona: $persona",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextSlate400,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss welcome greeting",
                        tint = BentoTextSlate400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Spoken Message Text
            Text(
                text = "“$message”",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextPrimary,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic
                )
            )

            // Audio Action Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSpeaking) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoRose.copy(alpha = 0.15f))
                            .border(1.dp, BentoRose.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { onStop() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_stop_welcome_speech")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                tint = BentoRose,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Stop Audio",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoRose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoIndigo.copy(alpha = 0.2f))
                            .border(1.dp, BentoIndigo.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onReplay() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("btn_replay_welcome_speech")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Play Greeting",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoIndigo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Text-to-Speech Engine",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BentoTextSlate500,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

