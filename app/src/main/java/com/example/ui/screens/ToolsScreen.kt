package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CopyButtonVariant
import com.example.ui.components.CopyToClipboardButton
import com.example.ui.components.InnovaCard
import com.example.ui.components.MarkdownText
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.InnovaGlassBorder
import com.example.ui.theme.InnovaPrimaryGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.viewmodel.FlashCard
import com.example.viewmodel.InnovaViewModel
import com.example.viewmodel.QuizQuestion
import com.example.viewmodel.StudioTab

@Composable
fun ToolsScreen(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentStudio by viewModel.currentStudio.collectAsState()

    val studios = listOf(
        Pair(StudioTab.WRITER, "Writing"),
        Pair(StudioTab.IMAGES, "AI Images"),
        Pair(StudioTab.CODE, "Code"),
        Pair(StudioTab.STUDY, "Study"),
        Pair(StudioTab.TRANSLATE, "Translate"),
        Pair(StudioTab.SUMMARIZE, "Summarize"),
        Pair(StudioTab.BRAINSTORM, "Brainstorm")
    )

    val selectedIndex = studios.indexOfFirst { it.first == currentStudio }.coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "AI Creation Studios",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Specialized AI engines for every task",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Studios Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = CyberDarkSurface,
            contentColor = NeonCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = NeonCyan,
                        height = 3.dp
                    )
                }
            }
        ) {
            studios.forEachIndexed { index, pair ->
                val isSelected = index == selectedIndex
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setStudioTab(pair.first) },
                    text = {
                        Text(
                            text = pair.second,
                            color = if (isSelected) NeonCyan else TextSecondaryDark,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // Studio Body Content
        Box(modifier = Modifier.weight(1f)) {
            when (currentStudio) {
                StudioTab.WRITER -> WriterStudioView(viewModel)
                StudioTab.IMAGES -> ImageGenerationScreen(viewModel = viewModel)
                StudioTab.CODE -> CodeStudioView(viewModel)
                StudioTab.STUDY -> StudyStudioView(viewModel)
                StudioTab.TRANSLATE -> TranslateStudioView(viewModel)
                StudioTab.SUMMARIZE -> SummarizeStudioView(viewModel)
                StudioTab.BRAINSTORM -> BrainstormStudioView(viewModel)
            }
        }
    }
}

// 1. Writer Studio View
@Composable
fun WriterStudioView(viewModel: InnovaViewModel) {
    val topic by viewModel.writerTopic.collectAsState()
    val type by viewModel.writerType.collectAsState()
    val tone by viewModel.writerTone.collectAsState()
    val result by viewModel.writerResult.collectAsState()
    val isGenerating by viewModel.isGeneratingWriter.collectAsState()

    val context = LocalContext.current
    val contentTypes = listOf("Blog Post", "Essay", "Email", "Story", "Headline", "Social Post")
    val tones = listOf("Professional", "Creative", "Concise", "Persuasive", "Academic", "Casual")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Content Type",
                        style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        contentTypes.forEach { t ->
                            val selected = t == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) ElectricPurple else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setWriterType(t) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = t,
                                    color = if (selected) Color.White else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "Tone & Voice",
                        style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tones.forEach { tn ->
                            val selected = tn == tone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) Color(0xFF3A86FF) else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setWriterTone(tn) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = tn,
                                    color = if (selected) Color.White else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { viewModel.setWriterTopic(it) },
                        label = { Text("What would you like to write about?", color = TextSecondaryDark) },
                        placeholder = { Text("e.g., The Future of Artificial General Intelligence and Human Agency", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.generateWriterContent() },
                        enabled = !isGenerating && topic.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Create, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Generate $type", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated Copy ($tone)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy to Clipboard",
                                    clipLabel = "Writer Copy",
                                    accentColor = ElectricPurple,
                                    testTag = "copy_writer_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Copy"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, null, tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}

// 2. Image Studio View
@Composable
fun ImageStudioView(viewModel: InnovaViewModel) {
    ImageGenerationScreen(viewModel = viewModel)
}

// 3. Code Studio View
@Composable
fun CodeStudioView(viewModel: InnovaViewModel) {
    val context = LocalContext.current
    val language by viewModel.codeLanguage.collectAsState()
    val task by viewModel.codeTask.collectAsState()
    val input by viewModel.codeInput.collectAsState()
    val result by viewModel.codeResult.collectAsState()
    val isGenerating by viewModel.isGeneratingCode.collectAsState()

    val languages = listOf("Kotlin", "Python", "JavaScript", "TypeScript", "SQL", "Rust", "C++", "Swift", "Go", "HTML/CSS")
    val tasks = listOf("Generate Code", "Debug & Fix", "Explain Code", "Refactor", "Unit Tests")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Programming Language", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { l ->
                            val selected = l == language
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) Color(0xFF00F0A8) else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setCodeLanguage(l) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = l,
                                    color = if (selected) Color.Black else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Text("Code Task", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tasks.forEach { t ->
                            val selected = t == task
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) ElectricPurple else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setCodeTask(t) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = t,
                                    color = if (selected) Color.White else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = input,
                        onValueChange = { viewModel.setCodeInput(it) },
                        label = { Text("Code requirement or paste snippet", color = TextSecondaryDark) },
                        placeholder = { Text("e.g., Write a Kotlin Flow debounce search with Room DAO cache", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = Color(0xFF00F0A8),
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.generateCode() },
                        enabled = !isGenerating && input.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0A8))
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Code, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Text("Execute $task", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code Result ($language)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color(0xFF00F0A8),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy Code",
                                    clipLabel = "Generated Code",
                                    accentColor = Color(0xFF00F0A8),
                                    testTag = "copy_code_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Code"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, "Share", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}

// 4. Study Studio View
@Composable
fun StudyStudioView(viewModel: InnovaViewModel) {
    val context = LocalContext.current
    val topic by viewModel.studyTopic.collectAsState()
    val mode by viewModel.studyMode.collectAsState()
    val result by viewModel.studyResult.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val selectedAnswers by viewModel.selectedQuizAnswers.collectAsState()
    val isGenerating by viewModel.isGeneratingStudy.collectAsState()

    val modes = listOf("Concept Explainer", "Flashcards", "Quiz")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Study Tool Mode", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modes.forEach { m ->
                            val selected = m == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) Color(0xFF9D4EDD) else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setStudyMode(m) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = m,
                                    color = if (selected) Color.White else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { viewModel.setStudyTopic(it) },
                        label = { Text("Subject or Concept to Learn", color = TextSecondaryDark) },
                        placeholder = { Text("e.g., Photosynthesis, Neural Networks, Game Theory, French Revolution", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = Color(0xFF9D4EDD),
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.generateStudyContent() },
                        enabled = !isGenerating && topic.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9D4EDD))
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Generate $mode", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Interactive Flashcards View
        if (flashcards.isNotEmpty()) {
            items(flashcards) { card ->
                FlashcardItemView(card = card)
            }
        }

        // Interactive Quiz View
        if (quizQuestions.isNotEmpty()) {
            item {
                Text(
                    text = "Interactive Knowledge Check",
                    style = MaterialTheme.typography.titleMedium.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                )
            }

            quizQuestions.forEachIndexed { qIdx, question ->
                item {
                    QuizQuestionCard(
                        question = question,
                        questionIndex = qIdx,
                        selectedOptionIndex = selectedAnswers[qIdx],
                        onSelectOption = { optIdx ->
                            viewModel.selectQuizAnswer(qIdx, optIdx)
                        }
                    )
                }
            }
        }

        if (result.isNotEmpty() && flashcards.isEmpty() && quizQuestions.isEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Concept Breakdown ($topic)",
                                style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF9D4EDD), fontWeight = FontWeight.Bold)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy to Clipboard",
                                    clipLabel = "Study Concept",
                                    accentColor = Color(0xFF9D4EDD),
                                    testTag = "copy_study_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Concept Breakdown"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, "Share", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardItemView(card: FlashCard) {
    var isFlipped by remember { mutableStateOf(false) }

    InnovaCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isFlipped = !isFlipped },
        backgroundColor = if (isFlipped) Color(0xFF1E143E) else CyberDarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFlipped) "RECALL / DEFINITION" else "TERM / QUESTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isFlipped) Color(0xFF00F0A8) else NeonCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Icon(
                    imageVector = Icons.Default.Flip,
                    contentDescription = "Flip card",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = if (isFlipped) card.definition else card.term,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )

            Text(
                text = "Tap anywhere to flip card",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF6E7092),
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    questionIndex: Int,
    selectedOptionIndex: Int?,
    onSelectOption: (Int) -> Unit
) {
    InnovaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Question ${questionIndex + 1}: ${question.question}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { optIdx, optText ->
                    val isSelected = selectedOptionIndex == optIdx
                    val isCorrect = question.correctIndex == optIdx
                    val showResult = selectedOptionIndex != null

                    val bgColor = when {
                        !showResult && isSelected -> ElectricPurple
                        showResult && isCorrect -> Color(0x3300F0A8)
                        showResult && isSelected && !isCorrect -> Color(0x33FF5252)
                        else -> CyberDarkSurface
                    }

                    val borderColor = when {
                        showResult && isCorrect -> Color(0xFF00F0A8)
                        showResult && isSelected && !isCorrect -> Color(0xFFFF5252)
                        isSelected -> NeonCyan
                        else -> Color(0xFF2E2458)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { onSelectOption(optIdx) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${('A'.code + optIdx).toChar()}) $optText",
                                color = if (showResult && isCorrect) Color(0xFF00F0A8) else TextPrimaryDark,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (showResult && isCorrect) {
                                Icon(Icons.Default.Check, null, tint = Color(0xFF00F0A8), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            if (selectedOptionIndex != null && question.explanation.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF130E26))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💡 Explanation: ${question.explanation}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonCyan,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

// 5. Translate Studio View
@Composable
fun TranslateStudioView(viewModel: InnovaViewModel) {
    val sourceText by viewModel.translateSourceText.collectAsState()
    val sourceLang by viewModel.translateSourceLang.collectAsState()
    val targetLang by viewModel.translateTargetLang.collectAsState()
    val result by viewModel.translateResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val context = LocalContext.current

    val languages = listOf("English", "Spanish", "French", "German", "Japanese", "Chinese", "Korean", "Hindi", "Italian", "Portuguese", "Russian", "Arabic")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LanguageSelectorDropdown("From", sourceLang, languages) { viewModel.setTranslateSourceLang(it) }
                        IconButton(onClick = { viewModel.swapTranslateLanguages() }) {
                            Icon(Icons.Default.SwapHoriz, "Swap languages", tint = NeonCyan)
                        }
                        LanguageSelectorDropdown("To", targetLang, languages) { viewModel.setTranslateTargetLang(it) }
                    }

                    OutlinedTextField(
                        value = sourceText,
                        onValueChange = { viewModel.setTranslateSourceText(it) },
                        label = { Text("Enter text to translate", color = TextSecondaryDark) },
                        placeholder = { Text("Type or paste any paragraph...", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = Color(0xFF3A86FF),
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.translateText() },
                        enabled = !isTranslating && sourceText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A86FF))
                    ) {
                        if (isTranslating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Translate, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Translate Now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Translation ($targetLang)",
                                style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3A86FF), fontWeight = FontWeight.Bold)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.voiceManager.speak(result) }) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, "Listen", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                }
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy",
                                    clipLabel = "Translation",
                                    accentColor = Color(0xFF3A86FF),
                                    testTag = "copy_translate_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Translation"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, "Share", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectorDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberDarkSurface)
                    .border(1.dp, Color(0xFF2E2458), RoundedCornerShape(10.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(text = selected, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CyberDarkSurfaceElevated)
        ) {
            options.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang, color = TextPrimaryDark) },
                    onClick = {
                        onSelect(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 6. Summarizer Studio View
@Composable
fun SummarizeStudioView(viewModel: InnovaViewModel) {
    val context = LocalContext.current
    val input by viewModel.summarizeInput.collectAsState()
    val result by viewModel.summarizeResult.collectAsState()
    val isSummarizing by viewModel.isSummarizing.collectAsState()
    var summaryType by remember { mutableStateOf("Executive Summary") }

    val formats = listOf("Executive Summary", "TL;DR Bullets", "Action Items")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Summary Format", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        formats.forEach { f ->
                            val selected = f == summaryType
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) Color(0xFF06D6A0) else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { summaryType = f }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = f,
                                    color = if (selected) Color.Black else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = input,
                        onValueChange = { viewModel.setSummarizeInput(it) },
                        label = { Text("Paste document, report, or article text", color = TextSecondaryDark) },
                        placeholder = { Text("Paste text here...", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = Color(0xFF06D6A0),
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.summarizeContent(summaryType) },
                        enabled = !isSummarizing && input.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0))
                    ) {
                        if (isSummarizing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Summarize, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Text("Analyze & Summarize", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = summaryType,
                                style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF06D6A0), fontWeight = FontWeight.Bold)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy to Clipboard",
                                    clipLabel = "AI Summary",
                                    accentColor = Color(0xFF06D6A0),
                                    testTag = "copy_summary_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Summary"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, "Share", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}

// 7. Brainstorm Studio View
@Composable
fun BrainstormStudioView(viewModel: InnovaViewModel) {
    val context = LocalContext.current
    val topic by viewModel.brainstormTopic.collectAsState()
    val category by viewModel.brainstormCategory.collectAsState()
    val result by viewModel.brainstormResult.collectAsState()
    val isBrainstorming by viewModel.isBrainstorming.collectAsState()

    val categories = listOf("Startup Ideas", "Marketing Angles", "Product Features", "Creative Writing", "Problem Solving")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Ideation Category", style = MaterialTheme.typography.labelLarge.copy(color = TextPrimaryDark))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { c ->
                            val selected = c == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) Color(0xFFFFD166) else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setBrainstormCategory(c) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = c,
                                    color = if (selected) Color.Black else TextSecondaryDark,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { viewModel.setBrainstormTopic(it) },
                        label = { Text("What area do you want to brainstorm on?", color = TextSecondaryDark) },
                        placeholder = { Text("e.g., Space tourism onboarding, AI personalized music playlists", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = Color(0xFFFFD166),
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    Button(
                        onClick = { viewModel.generateBrainstormIdeas() },
                        enabled = !isBrainstorming && topic.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166))
                    ) {
                        if (isBrainstorming) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Lightbulb, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Text("Spark Ideas", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            item {
                InnovaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Brainstorm Matrix ($category)",
                                style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFFFFD166), fontWeight = FontWeight.Bold)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CopyToClipboardButton(
                                    textToCopy = result,
                                    label = "Copy to Clipboard",
                                    clipLabel = "Brainstorm Ideas",
                                    accentColor = Color(0xFFFFD166),
                                    testTag = "copy_brainstorm_clipboard_button",
                                    variant = CopyButtonVariant.FILLED
                                )
                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, result)
                                            this.type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Brainstorm Ideas"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, "Share", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        MarkdownText(text = result, textColor = TextPrimaryDark)
                    }
                }
            }
        }
    }
}
