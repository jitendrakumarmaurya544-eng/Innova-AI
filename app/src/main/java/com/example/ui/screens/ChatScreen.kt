package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MessageEntity
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CategoryBadge
import com.example.ui.components.InnovaCard
import com.example.ui.components.InnovaLogoBadge
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
import com.example.viewmodel.AppTab
import com.example.viewmodel.InnovaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val activeConv by viewModel.activeConversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()

    var inputMessage by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showDocInsertSheet by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                attachedBitmap = bitmap
                Toast.makeText(context, "Image attached", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive or stream updates
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            val totalCount = messages.size + if (streamingText.isNotEmpty()) 1 else 0
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = activeConv?.title ?: "Innova AI Chat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isStreaming) NeonCyan else Color(0xFF00F0A8))
                        )
                        Text(
                            text = if (isStreaming) "Generating response..." else selectedModel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isStreaming) NeonCyan else TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.setTab(AppTab.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimaryDark
                    )
                }
            },
            actions = {
                if (isSpeaking) {
                    IconButton(onClick = { viewModel.voiceManager.stopSpeaking() }) {
                        AudioWaveformVisualizer(isWaving = true, barColor = NeonCyan)
                    }
                }

                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextPrimaryDark
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(CyberDarkSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = { Text("New Conversation", color = TextPrimaryDark) },
                        onClick = {
                            showMenu = false
                            viewModel.startNewConversation()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AutoAwesome, null, tint = NeonCyan)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (selectedModel.contains("flash")) "Switch to Gemini 3.1 Pro" else "Switch to Gemini 3.5 Flash",
                                color = TextPrimaryDark
                            )
                        },
                        onClick = {
                            showMenu = false
                            val next = if (selectedModel.contains("flash")) "gemini-3.1-pro-preview" else "gemini-3.5-flash"
                            viewModel.selectModel(next)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Export / Share Chat", color = TextPrimaryDark) },
                        onClick = {
                            showMenu = false
                            val fullChat = messages.joinToString("\n\n") { "${it.role.uppercase()}:\n${it.content}" }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, fullChat)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Chat Transcript"))
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Share, null, tint = TextSecondaryDark)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear Conversation", color = Color(0xFFFF5252)) },
                        onClick = {
                            showMenu = false
                            showClearDialog = true
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CyberDarkSurface
            )
        )

        // Main Chat Message Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty() && streamingText.isEmpty()) {
                // Empty state greeting
                ChatEmptyState(
                    onSelectPrompt = { prompt ->
                        viewModel.sendMessage(prompt)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Innova Response", msg.content))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            onSpeak = {
                                if (isSpeaking) {
                                    viewModel.voiceManager.stopSpeaking()
                                } else {
                                    viewModel.voiceManager.speak(msg.content)
                                }
                            },
                            onShare = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, msg.content)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Content"))
                            },
                            onBookmark = {
                                viewModel.bookmarkMessage(msg)
                            },
                            onRegenerate = {
                                viewModel.regenerateLastMessage()
                            }
                        )
                    }

                    // Active streaming token card
                    if (streamingText.isNotEmpty()) {
                        item {
                            StreamingAiMessageItem(streamingText = streamingText)
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips (when idle)
        if (!isStreaming && messages.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Elaborate with examples",
                    "Make it more concise",
                    "Provide code implementation",
                    "Summarize in 3 bullets",
                    "Translate to Spanish"
                ).forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberDarkSurfaceElevated)
                            .border(1.dp, Color(0xFF2E2458), RoundedCornerShape(16.dp))
                            .clickable { viewModel.sendMessage(suggestion) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonCyan,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Attached image preview banner
        if (attachedBitmap != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberDarkSurfaceElevated)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        bitmap = attachedBitmap!!.asImageBitmap(),
                        contentDescription = "Attached image",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = "Image attached for analysis",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimaryDark,
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(onClick = { attachedBitmap = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove image",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Bottom Chat Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CyberDarkSurface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Attach image button
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberDarkSurfaceElevated)
                            .testTag("chat_attach_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Attach image",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Attach Document / Sample snippet button
                    IconButton(
                        onClick = { showDocInsertSheet = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberDarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Insert Document/Snippet",
                            tint = Color(0xFFFF70A6),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Input Text Field
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = {
                            Text(
                                text = "Message Innova AI...",
                                color = TextSecondaryDark,
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurfaceElevated,
                            unfocusedContainerColor = CyberDarkSurfaceElevated,
                            focusedBorderColor = ElectricPurple,
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        maxLines = 4
                    )

                    // Voice Input / Waveform
                    IconButton(
                        onClick = {
                            if (isListening) {
                                viewModel.voiceManager.stopListening()
                            } else {
                                viewModel.voiceManager.startListening { spoken ->
                                    inputMessage = spoken
                                }
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFFFF2A85) else CyberDarkSurfaceElevated)
                            .testTag("chat_voice_record_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) Color.White else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Send or Stop Generation Button
                    if (isStreaming) {
                        IconButton(
                            onClick = { viewModel.stopStreaming() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252))
                                .testTag("chat_stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank() || attachedBitmap != null) {
                                    viewModel.sendMessage(inputMessage, attachedBitmap)
                                    inputMessage = ""
                                    attachedBitmap = null
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(InnovaPrimaryGradient)
                                .testTag("chat_send_button")
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
        }
    }

    // Document / Snippet Quick Insert Dialog
    if (showDocInsertSheet) {
        DocInsertDialog(
            onDismiss = { showDocInsertSheet = false },
            onInsert = { docText ->
                inputMessage = if (inputMessage.isBlank()) docText else "$inputMessage\n\n$docText"
                showDocInsertSheet = false
            }
        )
    }

    // Clear history confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Conversation", color = TextPrimaryDark) },
            text = { Text("Are you sure you want to clear this entire chat conversation?", color = TextSecondaryDark) },
            confirmButton = {
                TextButton(
                    onClick = {
                        activeConv?.let { viewModel.deleteConversation(it.id) }
                        showClearDialog = false
                        viewModel.setTab(AppTab.HOME)
                    }
                ) {
                    Text("Clear", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = CyberDarkSurfaceElevated
        )
    }
}

@Composable
fun ChatEmptyState(
    onSelectPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        InnovaLogoBadge(size = 64.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Innova AI Assistant",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your Ideas, Powered by AI. What would you like to build or explore today?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryDark,
                lineHeight = 20.sp,
                fontSize = 13.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Write an executive summary on the latest AI advancements",
                "Explain recursion in Kotlin with an interactive example",
                "Design a product launch roadmap for a SaaS mobile app",
                "Translate: 'Good design is as little design as possible' into French & Japanese"
            ).forEach { prompt ->
                InnovaCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberDarkSurfaceElevated,
                    onClick = { onSelectPrompt(prompt) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimaryDark,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: MessageEntity,
    onCopy: () -> Unit,
    onSpeak: () -> Unit,
    onShare: () -> Unit,
    onBookmark: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            // User Message Bubble
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 4.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6C5CE7), Color(0xFF3A86FF))
                        )
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        } else {
            // AI Message Bubble
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                InnovaLogoBadge(size = 32.dp, showGlow = false)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 20.dp,
                                    bottomStart = 20.dp,
                                    bottomEnd = 20.dp
                                )
                            )
                            .background(CyberDarkSurfaceElevated)
                            .border(1.dp, Color(0xFF281E48), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        MarkdownText(
                            text = message.content,
                            textColor = TextPrimaryDark
                        )
                    }

                    // Action buttons bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy message",
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(onClick = onSpeak, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read aloud",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(onClick = onBookmark, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = if (message.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save message",
                                tint = if (message.isSaved) Color(0xFFFFB703) else TextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(onClick = onShare, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        IconButton(onClick = onRegenerate, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate",
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreamingAiMessageItem(
    streamingText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        InnovaLogoBadge(size = 32.dp, showGlow = true)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .background(CyberDarkSurfaceElevated)
                    .border(1.dp, InnovaGlassBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    MarkdownText(
                        text = streamingText,
                        textColor = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = NeonCyan,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Streaming thoughts...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonCyan,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocInsertDialog(
    onDismiss: () -> Unit,
    onInsert: (String) -> Unit
) {
    var rawText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Insert Document or Text Content", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Paste text, notes, research papers, or contract excerpts for Innova to analyze:",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = { Text("Paste document text here...", color = TextSecondaryDark) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberDarkSurface,
                        unfocusedContainerColor = CyberDarkSurface,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF2E2458),
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (rawText.isNotBlank()) {
                        onInsert(rawText)
                    }
                }
            ) {
                Text("Insert into Prompt", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = CyberDarkSurfaceElevated
    )
}
