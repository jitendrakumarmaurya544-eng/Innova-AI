package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.preferences.UserProfile
import com.example.ui.components.InnovaCard
import com.example.ui.components.InnovaLogoBadge
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.InnovaGlassBorder
import com.example.ui.theme.InnovaPrimaryGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.viewmodel.InnovaViewModel

@Composable
fun ProfileScreen(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var name by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var title by remember(currentProfile) { mutableStateOf(currentProfile.title) }
    var persona by remember(currentProfile) { mutableStateOf(currentProfile.persona) }
    var instructions by remember(currentProfile) { mutableStateOf(currentProfile.customInstructions) }
    var model by remember(currentProfile) { mutableStateOf(currentProfile.preferredModel) }
    var temperature by remember(currentProfile) { mutableFloatStateOf(currentProfile.temperature) }
    var autoRead by remember(currentProfile) { mutableStateOf(currentProfile.autoReadAloud) }
    var ttsSpeed by remember(currentProfile) { mutableFloatStateOf(currentProfile.ttsSpeed) }

    val personas = listOf(
        PersonaInfo("Tech Luminary", "Engineering Depth & Clarity", Icons.Default.Psychology, NeonCyan),
        PersonaInfo("Friendly Tutor", "Step-by-step & Analogies", Icons.Default.School, Color(0xFF9D4EDD)),
        PersonaInfo("Executive Pro", "Crisp & Actionable Summaries", Icons.Default.Work, Color(0xFF3A86FF)),
        PersonaInfo("Creative Muse", "Vivid & Imaginative Storytelling", Icons.Default.Palette, Color(0xFFFF70A6)),
        PersonaInfo("Code Guru", "Senior Software Architecture", Icons.Default.Code, Color(0xFF00F0A8))
    )

    val hasApiKey = BuildConfig.GEMINI_API_KEY.isNotEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // User Header Card
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x338A2BE2), Color(0x1100D2FF), Color(0x00000000))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        InnovaLogoBadge(size = 56.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = name.ifBlank { "Explorer" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = title.ifBlank { "Innova AI Member" },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeonCyan,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Persona: $persona",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Identity & Persona Customization
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "AI Companion Persona",
                        style = MaterialTheme.typography.titleSmall.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        personas.forEach { p ->
                            val selected = p.name == persona
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selected) p.color.copy(alpha = 0.2f) else CyberDarkSurface)
                                    .border(1.dp, if (selected) p.color else Color(0xFF2E2458), RoundedCornerShape(14.dp))
                                    .clickable { persona = p.name }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(p.icon, null, tint = if (selected) p.color else TextSecondaryDark, modifier = Modifier.size(16.dp))
                                    Column {
                                        Text(
                                            text = p.name,
                                            color = if (selected) p.color else TextPrimaryDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = p.subtitle,
                                            color = TextSecondaryDark,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Your Role / Title", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF2E2458),
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Custom Directives / System Prompt", color = TextSecondaryDark) },
                        placeholder = { Text("How should Innova respond? (e.g. format as markdown, include code examples, keep brief)", color = Color(0xFF6E7092)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
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
            }
        }

        // Model & Intelligence Settings
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "AI Model & Engine Configuration",
                        style = MaterialTheme.typography.titleSmall.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("gemini-3.5-flash", "gemini-3.1-pro-preview").forEach { m ->
                            val selected = m == model
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) ElectricPurple else CyberDarkSurface)
                                    .border(1.dp, if (selected) NeonCyan else Color(0xFF2E2458), RoundedCornerShape(12.dp))
                                    .clickable { model = m }
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (m.contains("flash")) "Gemini 3.5 Flash" else "Gemini 3.1 Pro",
                                        color = if (selected) Color.White else TextPrimaryDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (m.contains("flash")) "Ultra fast & versatile" else "Deep reasoning & analysis",
                                        color = if (selected) Color(0xFFE2E8F0) else TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Temperature / Creativity", color = TextSecondaryDark, fontSize = 13.sp)
                            Text(text = "%.1f".format(temperature), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Slider(
                            value = temperature,
                            onValueChange = { temperature = it },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = ElectricPurple,
                                inactiveTrackColor = Color(0xFF2E2458)
                            )
                        )
                    }
                }
            }
        }

        // Voice & Speech Settings
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Voice & Speech Settings",
                        style = MaterialTheme.typography.titleSmall.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Auto-Read Responses Aloud", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Speak AI replies automatically via TTS", color = TextSecondaryDark, fontSize = 12.sp)
                        }

                        Switch(
                            checked = autoRead,
                            onCheckedChange = { autoRead = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = TextSecondaryDark,
                                uncheckedTrackColor = CyberDarkSurface
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Speech Rate (TTS Speed)", color = TextSecondaryDark, fontSize = 13.sp)
                            Text(text = "%.1fx".format(ttsSpeed), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Slider(
                            value = ttsSpeed,
                            onValueChange = { ttsSpeed = it },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = ElectricPurple,
                                inactiveTrackColor = Color(0xFF2E2458)
                            )
                        )
                    }
                }
            }
        }

        // API Key Status Banner
        item {
            InnovaCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (hasApiKey) Color(0x2200F0A8) else Color(0x22FFB703)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = if (hasApiKey) Color(0xFF00F0A8) else Color(0xFFFFB703),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasApiKey) "Gemini API Key Active" else "API Key Configuration",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (hasApiKey) "Direct Cloud AI streaming enabled" else "Provide GEMINI_API_KEY via AI Studio Secrets Panel",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Replay AI Welcome Greeting Speech
        item {
            InnovaCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.playWelcomeSpeech(forceReplay = true)
                    Toast.makeText(context, "Playing AI spoken welcome greeting...", Toast.LENGTH_SHORT).show()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x2210B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Play AI Welcome Greeting",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Listen to your customized AI voice introduction",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Replay Onboarding Button
        item {
            InnovaCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.replayOnboarding()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x226366F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Revisit Onboarding Guide",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Walk through the feature overview and setup guide again",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Save Profile Button
        item {
            Button(
                onClick = {
                    val updated = UserProfile(
                        name = name,
                        title = title,
                        persona = persona,
                        customInstructions = instructions,
                        preferredModel = model,
                        temperature = temperature,
                        autoReadAloud = autoRead,
                        ttsSpeed = ttsSpeed
                    )
                    viewModel.updateUserProfile(updated)
                    Toast.makeText(context, "Preferences saved successfully", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("Save & Apply Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

data class PersonaInfo(
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)
