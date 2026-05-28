package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.ui.FusionActionCenter
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

private val NavigationBrand = Color(0xFFD1143A)
private val NavigationInk = Color(0xFF141418)

@Composable
fun MainTabsScreen(
    navController: NavController,
    photoViewModel: PhotoViewModel,
    initialTab: Int = 0
) {
    val tabs =
        listOf(
            MainTab("Home", Icons.Rounded.Home),
            MainTab("Historial", Icons.Rounded.History),
            MainTab("Conexi\u00f3n", Icons.Rounded.Link)
        )

    var selectedTab by rememberSaveable {
        mutableStateOf(initialTab.coerceIn(tabs.indices))
    }

    LaunchedEffect(initialTab) {
        selectedTab = initialTab.coerceIn(tabs.indices)
    }

    val fusionActionState by FusionActionCenter.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 ->
                        PhotoGalleryScreen(
                            navController = navController,
                            viewModel = photoViewModel,
                            applyStatusBarPadding = false
                        )

                    1 ->
                        FusionHistoryScreen(
                            navController = navController,
                            applyStatusBarPadding = false
                        )

                    else ->
                        ConnectionsScreen()
                }
            }

            NavigationBar(
                containerColor = Color.White,
                contentColor = NavigationInk,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(text = tab.title)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = NavigationBrand,
                            indicatorColor = NavigationBrand,
                            unselectedIconColor = NavigationInk.copy(alpha = 0.58f),
                            unselectedTextColor = NavigationInk.copy(alpha = 0.58f)
                        )
                    )
                }
            }
        }

        if (fusionActionState.isProcessing) {
            PublishingActivityBanner(
                title = fusionActionState.title ?: "Procesando",
                message = fusionActionState.message ?: "Puedes seguir navegando."
            )
        }

        if (fusionActionState.resultTitle != null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(fusionActionState.resultTitle.orEmpty()) },
                text = { Text(fusionActionState.resultMessage.orEmpty()) },
                confirmButton = {
                    TextButton(onClick = { FusionActionCenter.dismissResult() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun PublishingActivityBanner(
    title: String,
    message: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 30.dp),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                color = NavigationBrand,
                strokeWidth = 3.dp
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavigationInk
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = NavigationInk.copy(alpha = 0.62f)
                )
            }
        }
    }
}

private data class MainTab(
    val title: String,
    val icon: ImageVector
)
