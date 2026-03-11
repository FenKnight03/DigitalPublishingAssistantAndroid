package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.ui.components.DistributorCard
import com.ljdit.digitalpublishing.viewmodel.DistributorViewModel

@Composable
fun DistributorSelectionScreen(
    navController: NavController,
    photoId: String?,
    viewModel: DistributorViewModel = viewModel()
) {

    val distributors by viewModel.distributors.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {

        items(distributors) { distributor ->

            DistributorCard(
                distributor = distributor,
                onClick = {

                    navController.navigate(
                        "coordinates/$photoId/${distributor.id}"
                    )

                }
            )

        }

    }
}