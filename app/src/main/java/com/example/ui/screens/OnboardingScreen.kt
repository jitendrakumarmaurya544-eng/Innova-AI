package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.viewmodel.InnovaViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    // Form inputs for Step 4
    var userName by remember { mutableStateOf(userProfile.name) }
    var userTitle by remember { mutableStateOf(userProfile.title) }
    var selectedPersona by remember { mutableStateOf(userProfile.persona) }
    var selectedInterests by remember {
        mutableStateOf(setOf("AI Chat", "Code & Dev", "Creative Writing", "Image Generation"))
    }
    var firstSparkPrompt by remember { mutableStateOf("") }

    val personaOptions = listOf(
        Triple("Tech Luminary", "Futuristic, visionary & engineering-deep", Icons.Default.AutoAwesome),
        Triple("Code Guru", "Senior staff software architect & concise", Icons.Default.Code),
        Triple("Creative Muse", "Vibrant, imaginative & storytelling", Icons.Default.Create),
        Triple("Executive Pro", "Crisp executive summaries & strategy", Icons.Default.Psychology),
        Triple("Friendly Tutor", "Patient, step-by-step & encouraging", Icons.Default.School)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Navigation & Step Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InnovaLogoBadge(size = 32.dp, showGlow = false)
                    Text(
                        text = "INNOVA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Step Counter Capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x14FFFFFF))
                        .border(1.dp, BentoBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Step ${currentStep + 1} of $totalSteps",
                        color = BentoTextSlate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Skip Action Button
                if (currentStep < totalSteps - 1) {
                    TextButton(
                        onClick = {
                            viewModel.completeOnboarding(
                                name = userName,
                                title = userTitle,
                                persona = selectedPersona,
                                interests = selectedInterests
                            )
                        },
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            color = BentoTextSlate400,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Step Progress Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(totalSteps) { stepIndex ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (stepIndex <= currentStep) BentoIndigo else Color(0x1FFFFFFF)
                            )
                    )
                }
            }

            // Animated Step Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    },
                    label = "OnboardingStepTransition"
                ) { step ->
                    when (step) {
                        0 -> OnboardingStepChat(
                            onQuickPrompt = { prompt ->
                                firstSparkPrompt = prompt
                                currentStep = 3
                            }
                        )
                        1 -> OnboardingStepStudios(
                            selectedInterests = selectedInterests,
                            onToggleInterest = { interest ->
                                selectedInterests = if (selectedInterests.contains(interest)) {
                                    if (selectedInterests.size > 1) selectedInterests - interest else selectedInterests
                                } else {
                                    selectedInterests + interest
                                }
                            }
                        )
                        2 -> OnboardingStepImageGen()
                        3 -> OnboardingStepSetup(
                            userName = userName,
                            onNameChange = { userName = it },
                            userTitle = userTitle,
                            onTitleChange = { userTitle = it },
                            selectedPersona = selectedPersona,
                            onSelectPersona = { selectedPersona = it },
                            personaOptions = personaOptions,
                            firstSparkPrompt = firstSparkPrompt,
                            onPromptChange = { firstSparkPrompt = it }
                        )
                    }
                }
            }

            // Bottom Navigation Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    IconButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BentoCardSurface)
                            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(16.dp))
                            .testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BentoTextSlate300,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Next or Complete Button
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            viewModel.completeOnboarding(
                                name = userName,
                                title = userTitle,
                                persona = selectedPersona,
                                interests = selectedInterests,
                                initialPrompt = firstSparkPrompt.ifBlank { null }
                            )
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f)
                        .padding(start = 12.dp)
                        .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = BentoIndigo)
                        .testTag(if (currentStep == totalSteps - 1) "onboarding_finish_button" else "onboarding_next_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoIndigo)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentStep == totalSteps - 1) "Launch Innova AI" else "Continue",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = if (currentStep == totalSteps - 1) Icons.Default.RocketLaunch else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 1: Real-Time AI Chat & Multimodal
// -------------------------------------------------------------
@Composable
fun OnboardingStepChat(
    onQuickPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BentoCardSurface)
                .border(1.dp, BentoBorderSubtle, RoundedCornerShape(26.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding_chat),
                contentDescription = "AI Chat Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BentoCardSurface.copy(alpha = 0.85f))
                        )
                    )
            )
            // Floating Feature Pill
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xCC05050A))
                    .border(1.dp, BentoIndigo.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BentoIndigo,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Gemini 3.5 Flash & 3.1 Pro",
                        color = BentoTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Title & Description
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Conversational Intelligence",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Chat seamlessly with deep context memory, multimodal image & document understanding, and natural voice readout.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSlate400,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }

        // Key Capabilities Bento Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "KEY CAPABILITIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OnboardingFeatureChip(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    title = "Multi-turn Chat",
                    desc = "Real-time streaming",
                    color = BentoIndigo,
                    modifier = Modifier.weight(1f)
                )
                OnboardingFeatureChip(
                    icon = Icons.Default.Mic,
                    title = "Voice Synthesis",
                    desc = "Speech-to-Text & Readout",
                    color = BentoSky,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Interactive Spark Sample Prompts
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "TRY A STARTER PROMPT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            listOf(
                "Explain the architecture of Transformer models in simple terms",
                "How do I optimize Android app performance with Kotlin Flow?"
            ).forEach { prompt ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoCardSurface)
                        .border(1.dp, BentoBorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { onQuickPrompt(prompt) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "\"$prompt\"",
                            color = BentoTextSlate300,
                            fontSize = 12.sp,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BentoIndigo,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 2: Content Creation & Specialized Studios
// -------------------------------------------------------------
@Composable
fun OnboardingStepStudios(
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BentoCardSurface)
                .border(1.dp, BentoBorderSubtle, RoundedCornerShape(26.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding_creation),
                contentDescription = "Content Creation Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BentoCardSurface.copy(alpha = 0.85f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xCC05050A))
                    .border(1.dp, BentoEmerald.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = BentoEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "7 Specialized AI Studios",
                        color = BentoTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Title & Description
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Specialized AI Studios",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Go beyond chat with tailored workflows for code generation, copywriting, flashcards, interactive quizzes, and document summaries.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSlate400,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }

        // Studio Tiles Bento Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "WHAT WILL YOU USE INNOVA FOR?",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            val studios = listOf(
                StudioInterest("Code & Dev", "Write, debug & explain algorithms", Icons.Default.Code, BentoEmerald),
                StudioInterest("Creative Writing", "Blogs, pitches, essays & copy", Icons.Default.Create, BentoRose),
                StudioInterest("Study & Quizzes", "Flashcards & adaptive testing", Icons.Default.School, BentoViolet),
                StudioInterest("Summarizer", "PDF & document analysis", Icons.Default.Summarize, BentoSky),
                StudioInterest("Brainstorming", "Disruptive startup ideation", Icons.Default.Lightbulb, BentoAmber),
                StudioInterest("Polyglot", "30+ language translation", Icons.Default.Translate, BentoIndigo)
            )

            studios.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { studio ->
                        val isSelected = selectedInterests.contains(studio.name)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) studio.accentColor.copy(alpha = 0.16f) else BentoCardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) studio.accentColor.copy(alpha = 0.6f) else BentoBorderSubtle,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { onToggleInterest(studio.name) }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(studio.accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = studio.icon,
                                        contentDescription = studio.name,
                                        tint = studio.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = studio.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = BentoTextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = studio.desc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = BentoTextSlate400,
                                            fontSize = 9.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = studio.accentColor,
                                        modifier = Modifier.size(14.dp)
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

private data class StudioInterest(
    val name: String,
    val desc: String,
    val icon: ImageVector,
    val accentColor: Color
)

// -------------------------------------------------------------
// Step 3: High-Fidelity Image Generation
// -------------------------------------------------------------
@Composable
fun OnboardingStepImageGen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BentoCardSurface)
                .border(1.dp, BentoBorderSubtle, RoundedCornerShape(26.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding_art),
                contentDescription = "AI Image Generation Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BentoCardSurface.copy(alpha = 0.85f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xCC05050A))
                    .border(1.dp, BentoAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = BentoAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Text-to-Image Synthesis",
                        color = BentoTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Title & Description
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Generative Image Studio",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Create vivid concept art, futuristic illustrations, 3D renders, and product mockups directly from natural language descriptions.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSlate400,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }

        // Curated Aesthetic Presets
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CREATIVE AESTHETICS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            val styles = listOf(
                "Futuristic Neon" to BentoIndigo,
                "Cyberpunk" to BentoRose,
                "3D Isometric" to BentoEmerald,
                "Digital Art" to BentoAmber,
                "Photorealistic" to BentoSky
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                styles.take(3).forEach { (style, color) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BentoCardSurface)
                            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style,
                            color = color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                styles.drop(3).forEach { (style, color) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BentoCardSurface)
                            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style,
                            color = color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 4: Setup AI Persona & First Interaction
// -------------------------------------------------------------
@Composable
fun OnboardingStepSetup(
    userName: String,
    onNameChange: (String) -> Unit,
    userTitle: String,
    onTitleChange: (String) -> Unit,
    selectedPersona: String,
    onSelectPersona: (String) -> Unit,
    personaOptions: List<Triple<String, String, ImageVector>>,
    firstSparkPrompt: String,
    onPromptChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Personalize Your AI",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Tailor Innova's voice, tone, and identity to best match your workflow.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSlate400,
                    fontSize = 13.sp
                )
            )
        }

        // Profile Details Input
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "YOUR PROFILE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = onNameChange,
                    label = { Text("Your Name", fontSize = 12.sp, color = BentoTextSlate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorderSubtle,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary,
                        focusedContainerColor = BentoCardSurface,
                        unfocusedContainerColor = BentoCardSurface
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("onboarding_name_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = userTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Your Role", fontSize = 12.sp, color = BentoTextSlate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorderSubtle,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary,
                        focusedContainerColor = BentoCardSurface,
                        unfocusedContainerColor = BentoCardSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_title_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }
        }

        // Persona Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CHOOSE AI PERSONA",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            personaOptions.forEach { (personaName, personaDesc, personaIcon) ->
                val isSelected = selectedPersona == personaName
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) BentoIndigo.copy(alpha = 0.16f) else BentoCardSurface)
                        .border(
                            1.dp,
                            if (isSelected) BentoIndigo else BentoBorderSubtle,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onSelectPersona(personaName) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BentoIndigo else Color(0x14FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = personaIcon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else BentoTextSlate300,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = personaName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = BentoTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = personaDesc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextSlate400,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = BentoIndigo,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Initial Spark Prompt
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "FIRST PROMPT (OPTIONAL)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSlate500,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            OutlinedTextField(
                value = firstSparkPrompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = "e.g. Brainstorm 3 innovative mobile app ideas",
                        color = BentoTextSlate500,
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoIndigo,
                    unfocusedBorderColor = BentoBorderSubtle,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary,
                    focusedContainerColor = BentoCardSurface,
                    unfocusedContainerColor = BentoCardSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_spark_input"),
                shape = RoundedCornerShape(16.dp),
                maxLines = 2
            )
        }
    }
}

@Composable
fun OnboardingFeatureChip(
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(BentoCardSurface)
            .border(1.dp, BentoBorderSubtle, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = BentoTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BentoTextSlate400,
                    fontSize = 10.sp
                )
            )
        }
    }
}
