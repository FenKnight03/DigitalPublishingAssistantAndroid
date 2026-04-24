package com.ljdit.digitalpublishing.ui.screens

import com.ljdit.digitalpublishing.ui.components.FusionItemView
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel

@Composable
fun FusionHistoryScreen(
    navController: NavController,
    viewModel: FusionViewModel = viewModel()
) {

    val fusions by viewModel.fusions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFusions()
    }

    val tabs = listOf("Pendientes", "Agendadas", "Publicadas")
    var selectedTab by remember { mutableStateOf(0) }

    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = { Text(title) }
            )
        }
    }
    val currentList = when (selectedTab) {
        0 -> fusions?.pendientes
        1 -> fusions?.agendadas
        else -> fusions?.publicadas
    }
    LazyColumn {
        items(currentList ?: emptyList()) { fusion ->
            FusionItemView(fusion) {
                navController.navigate("preview_from_history/${fusion.id}")
            }
        }
    }
}