package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BentoDarkBg
import com.example.ui.theme.BentoDeepSurface
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSlate400
import com.example.ui.theme.BentoTextSlate500
import com.example.viewmodel.AppTab
import com.example.viewmodel.InnovaViewModel

@Composable
fun InnovaMainView(
    viewModel: InnovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    Crossfade(
        targetState = isOnboardingCompleted,
        animationSpec = tween(400),
        label = "onboarding_main_crossfade"
    ) { completed ->
        if (!completed) {
            OnboardingScreen(viewModel = viewModel, modifier = modifier)
        } else {
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .background(BentoDarkBg)
                    .statusBarsPadding(),
                bottomBar = {
                    InnovaBottomNavigationBar(
                        currentTab = currentTab,
                        onSelectTab = { viewModel.setTab(it) }
                    )
                },
                containerColor = BentoDarkBg
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = currentTab,
                        animationSpec = tween(250),
                        label = "screen_crossfade"
                    ) { tab ->
                        when (tab) {
                            AppTab.HOME -> HomeScreen(viewModel = viewModel)
                            AppTab.CHAT -> ChatScreen(viewModel = viewModel)
                            AppTab.STUDIOS -> ToolsScreen(viewModel = viewModel)
                            AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                            AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val tab: AppTab,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun InnovaBottomNavigationBar(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    val items = listOf(
        NavItem(AppTab.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItem(AppTab.CHAT, "Chat", Icons.AutoMirrored.Filled.Chat, "nav_chat"),
        NavItem(AppTab.STUDIOS, "Studios", Icons.Default.AutoAwesome, "nav_studios"),
        NavItem(AppTab.HISTORY, "History", Icons.Default.History, "nav_history"),
        NavItem(AppTab.PROFILE, "Profile", Icons.Default.Person, "nav_profile")
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = BentoDeepSurface,
        tonalElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BentoBorderSubtle, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.tab == currentTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .background(BentoIndigo.copy(alpha = 0.2f))
                                        .border(1.dp, BentoIndigo.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onSelectTab(item.tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag(item.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) BentoIndigo else BentoTextSlate500,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BentoTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

