package com.ljdit.digitalpublishing.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.ljdit.digitalpublishing.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "gallery"
    ) {

        composable("gallery") {
            PhotoGalleryScreen(navController)
        }

        composable("distributors/{photoId}") { backStackEntry ->

            val photoId = backStackEntry.arguments?.getString("photoId")

            DistributorSelectionScreen(
                navController = navController,
                photoId = photoId
            )

        }

        composable("coordinates/{photoId}/{distributorId}") { backStackEntry ->

            val photoId = backStackEntry.arguments?.getString("photoId")
            val distributorId = backStackEntry.arguments?.getString("distributorId")

            CoordinateSelectionScreen(
                photoId = photoId,
                distributorId = distributorId,
                navController = navController
            )

        }

        composable("preview/{photoId}/{distributorId}/{coordinate}") { backStackEntry ->

            val photoId = backStackEntry.arguments?.getString("photoId")
            val distributorId = backStackEntry.arguments?.getString("distributorId")
            val coordinate = backStackEntry.arguments?.getString("coordinate")

            FusionPreviewScreen(
                photoId = photoId,
                distributorId = distributorId,
                coordinate = coordinate
            )

        }

    }
}