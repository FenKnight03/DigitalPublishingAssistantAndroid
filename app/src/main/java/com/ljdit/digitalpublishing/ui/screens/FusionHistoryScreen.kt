package com.ljdit.digitalpublishing.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.model.FusionItem
import com.ljdit.digitalpublishing.ui.components.FusionItemView
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel

private fun FusionItem.belongsToCurrentDistributor(): Boolean {
    val currentDistributorId = SessionManager.distributorId

    if (
        currentDistributorId != null &&
        distributorId != null
    ) {
        return distributorId == currentDistributorId
    }

    val currentDistributorName =
        SessionManager.distributorName
            ?.trim()
            ?.lowercase()

    if (!currentDistributorName.isNullOrBlank()) {
        return distributor_name
            .trim()
            .lowercase() == currentDistributorName
    }

    return SessionManager.isAdmin
}

@Composable
fun FusionHistoryScreen(
    navController: NavController,
    viewModel: FusionViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(color = White)
        ) {
            val fusions by viewModel.fusions.collectAsState()

            Log.d("FUSIONS_DEBUG", fusions.toString())

            LaunchedEffect(Unit) {
                viewModel.loadFusions()
            }

            val tabs = listOf("Pendientes", "Agendadas", "Publicadas")
            var selectedTab by remember { mutableStateOf(0) }

            Column(modifier = Modifier.fillMaxSize()) {
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
                }?.filter { it.belongsToCurrentDistributor() }

                val isCurrentTabActionable =
                    selectedTab == 0

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentList ?: emptyList()) { fusion ->
                        FusionItemView(
                            fusion = fusion,
                            isActionable = isCurrentTabActionable,
                            onClick = {
                                if (isCurrentTabActionable) {
                                    navController.navigate(
                                        "preview_from_history/${fusion.id}"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
