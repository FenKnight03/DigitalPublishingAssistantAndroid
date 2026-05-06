package com.ljdit.digitalpublishing.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.ljdit.digitalpublishing.ui.screens.*
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val photoViewModel: PhotoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "gallery"
    ) {

        composable("gallery") {
            PhotoGalleryScreen(
                navController = navController,
                viewModel = photoViewModel
            )
        }

        composable("preview/{photoId}/{distributorId}/{coordinate}") { backStackEntry ->

            val photoId = backStackEntry.arguments?.getString("photoId")
            val distributorId = backStackEntry.arguments?.getString("distributorId")
            val coordinate = backStackEntry.arguments?.getString("coordinate")

            FusionPreviewScreen(
                navController = navController,
                photoId = photoId,
                distributorId = distributorId,
                coordinate = coordinate
            )

        }

        composable("fusion_history") {
            FusionHistoryScreen(navController)
        }

        composable("preview_from_history/{fusionId}") { backStackEntry ->

            val fusionId = backStackEntry.arguments
                ?.getString("fusionId")
                ?: return@composable

            FusionPreviewScreen(
                navController = navController,
                fusionId = fusionId,
                fromHistory = true
            )
        }


        composable(
            "viewer/{photoId}"
        ) { backStackEntry ->

            val photoId =
                backStackEntry.arguments?.getString("photoId")

            PhotoViewerScreen(
                navController = navController,
                photoId = photoId,
                photoViewModel = photoViewModel
            )
        }


    }
}
