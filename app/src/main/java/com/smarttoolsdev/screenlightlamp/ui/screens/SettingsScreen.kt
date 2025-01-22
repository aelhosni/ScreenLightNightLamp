// SettingsScreen.kt
package com.smarttoolsdev.screenlightlamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.R
import com.smarttoolsdev.screenlightlamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onTutorialClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onShareClick: () -> Unit,
    onRateClick: () -> Unit
) {
    var autoTransitionEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsGroup(title = "Profile") {
                SettingsItem(
                    icon = R.drawable.ic_profile,
                    title = "Profile",
                    subtitle = "No Profile"
                )

                SettingsItem(
                    icon = R.drawable.ic_bookmark,
                    title = "Bookmark",
                    subtitle = "No Bookmark"
                )

                SettingsItem(
                    icon = R.drawable.ic_transition,
                    title = "Auto Transition",
                    hasSwitch = true,
                    switchValue = autoTransitionEnabled,
                    onSwitchChange = { autoTransitionEnabled = it }
                )
            }

            SettingsItem(
                icon = R.drawable.ic_premium,
                title = "Buy Premium",
                isHighlighted = true
            )

            SettingsGroup {
                SettingsItem(
                    icon = R.drawable.ic_tutorial,
                    title = "Tutorial",
                    onClick = onTutorialClick
                )

                SettingsItem(
                    icon = R.drawable.ic_restore,
                    title = "Restore Colors"
                )

                SettingsItem(
                    icon = R.drawable.ic_rate,
                    title = "Rate 5 star",
                    onClick = onRateClick
                )

                SettingsItem(
                    icon = R.drawable.ic_share,
                    title = "Share",
                    onClick = onShareClick
                )
            }

            SettingsGroup {
                SettingsItem(
                    icon = R.drawable.ic_privacy,
                    title = "Privacy Policy",
                    onClick = onPrivacyPolicyClick
                )

                SettingsItem(
                    icon = R.drawable.ic_info,
                    title = "Credits",
                    onClick = onCreditsClick
                )

                SettingsItem(
                    icon = R.drawable.ic_exit,
                    title = "Exit"
                )
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
    isHighlighted: Boolean = false,
    hasSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = if (isHighlighted) GoldYellow else Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = title,
                        color = if (isHighlighted) GoldYellow else Color.White
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (hasSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldYellow,
                        checkedTrackColor = GoldYellow.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}