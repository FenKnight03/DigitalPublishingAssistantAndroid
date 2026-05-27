package com.ljdit.digitalpublishing.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.model.FusionItem
import com.ljdit.digitalpublishing.ui.components.FusionItemView
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel

private val HistoryBrand = Color(0xFFD1143A)
private val HistoryInk = Color(0xFF141418)
private val HistorySoftInk = Color(0xFF5D6675)
private val HistoryCanvas = Color(0xFFF2F4F8)
private val HistoryField = Color(0xFFF3F3F6)

private fun FusionItem.belongsToCurrentDistributor(): Boolean {
    val currentDistributorId = SessionManager.distributorId

    if (currentDistributorId != null && distributorId != null) {
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
    viewModel: FusionViewModel = viewModel(),
    applyStatusBarPadding: Boolean = true
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = HistoryCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .then(
                    if (applyStatusBarPadding) {
                        Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    } else {
                        Modifier
                    }
                )
                .fillMaxSize()
                .background(HistoryCanvas)
        ) {
            val fusions by viewModel.fusions.collectAsState()

            Log.d("FUSIONS_DEBUG", fusions.toString())

            LaunchedEffect(Unit) {
                viewModel.loadFusions()
            }

            val filters = PublicationFilter.entries
            var selectedTab by remember { mutableStateOf(0) }
            val selectedFilter = filters[selectedTab]

            val currentList = when (selectedFilter) {
                PublicationFilter.Pending -> fusions?.pendientes
                PublicationFilter.Scheduled -> fusions?.agendadas
                PublicationFilter.Published -> fusions?.publicadas
            }?.filter { it.belongsToCurrentDistributor() }.orEmpty()

            val isCurrentTabActionable = selectedFilter == PublicationFilter.Pending

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = HistoryCanvas,
                contentColor = HistoryBrand
            ) {
                filters.forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = filter.title,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        selectedContentColor = HistoryBrand,
                        unselectedContentColor = HistorySoftInk
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    top = 14.dp,
                    end = 16.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    SummaryCard(
                        filter = selectedFilter,
                        itemCount = currentList.size
                    )
                }

                if (currentList.isEmpty()) {
                    item {
                        EmptyHistoryCard(filter = selectedFilter)
                    }
                } else {
                    item {
                        SectionEyebrow(
                            title = selectedFilter.sectionTitle,
                            icon = Icons.Rounded.Collections
                        )
                    }

                    items(currentList) { fusion ->
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

@Composable
private fun SummaryCard(
    filter: PublicationFilter,
    itemCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(HistoryBrand.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = filter.icon,
                    contentDescription = null,
                    tint = HistoryBrand
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = filter.sectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HistoryInk
                )

                Text(
                    text = filter.summaryText(itemCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HistorySoftInk
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard(filter: PublicationFilter) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = filter.icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = HistoryBrand
            )

            Text(
                text = filter.emptyTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HistoryInk
            )

            Text(
                text = "Cuando haya publicaciones en este estado, apareceran organizadas aqui.",
                style = MaterialTheme.typography.bodyMedium,
                color = HistorySoftInk
            )
        }
    }
}

@Composable
private fun SectionEyebrow(
    title: String,
    icon: ImageVector
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = HistorySoftInk
        )

        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = HistorySoftInk
        )
    }
}

private enum class PublicationFilter(
    val title: String,
    val sectionTitle: String,
    val emptyTitle: String,
    val icon: ImageVector
) {
    Pending(
        title = "Pendientes",
        sectionTitle = "Pendientes",
        emptyTitle = "No hay pendientes",
        icon = Icons.Rounded.History
    ),
    Scheduled(
        title = "Agendadas",
        sectionTitle = "Programadas",
        emptyTitle = "No hay agendadas",
        icon = Icons.Rounded.Event
    ),
    Published(
        title = "Publicadas",
        sectionTitle = "Publicadas",
        emptyTitle = "No hay publicadas",
        icon = Icons.Rounded.CheckCircle
    );

    fun summaryText(count: Int): String {
        return when (this) {
            Pending ->
                if (count == 1) {
                    "1 publicacion lista para revisar."
                } else {
                    "$count publicaciones listas para revisar."
                }

            Scheduled ->
                if (count == 1) {
                    "1 publicacion programada."
                } else {
                    "$count publicaciones programadas."
                }

            Published ->
                if (count == 1) {
                    "1 publicacion enviada."
                } else {
                    "$count publicaciones enviadas."
                }
        }
    }
}
