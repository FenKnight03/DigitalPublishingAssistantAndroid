package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

@Composable
fun MainTabsScreen(
    navController: NavController,
    photoViewModel: PhotoViewModel,
    initialTab: Int = 0
) {
    val tabs =
        listOf(
            "Galeria",
            "Historial",
            "Conexiones"
        )

    var selectedTab by rememberSaveable {
        mutableStateOf(initialTab.coerceIn(tabs.indices))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

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
}
