package com.ljdit.digitalpublishing.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.ljdit.digitalpublishing.model.PhotoPlatform
import com.ljdit.digitalpublishing.model.displayPlatforms
import com.ljdit.digitalpublishing.ui.screens.*
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val photoViewModel: PhotoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            MainTabsScreen(
                navController = navController,
                photoViewModel = photoViewModel
            )
        }

        composable("home/{initialTab}") { backStackEntry ->
            MainTabsScreen(
                navController = navController,
                photoViewModel = photoViewModel,
                initialTab =
                    backStackEntry.arguments
                        ?.getString("initialTab")
                        ?.toIntOrNull()
                        ?: 0
            )
        }

        composable("gallery") {
            LaunchedEffect(Unit) {
                navController.navigate("home") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        composable("preview/{photoId}/{logoId}/{coordinate}") { backStackEntry ->

            val photoId = backStackEntry.arguments?.getString("photoId")
            val logoId = backStackEntry.arguments?.getString("logoId")
            val coordinate = backStackEntry.arguments?.getString("coordinate")

            val photo = photoViewModel.photos.value
                .firstOrNull { it.id.toString() == photoId }
            val platforms = photo
                ?.displayPlatforms()
                ?.takeIf { it.isNotEmpty() }
                ?: platformsForFusionFormat(photo?.formato)

            FusionPreviewScreen(
                navController = navController,
                photoId = photoId,
                logoId = logoId,
                coordinate = coordinate,
                platforms = platforms
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
                fromHistory = true,
                returnToHistory = true,
                platforms = emptyList()
            )
        }

        composable("preview_from_history/{fusionId}/{format}") { backStackEntry ->

            val fusionId = backStackEntry.arguments
                ?.getString("fusionId")
                ?: return@composable

            val format = backStackEntry.arguments?.getString("format")
                ?: navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("fusion_format_$fusionId")

            FusionPreviewScreen(
                navController = navController,
                fusionId = fusionId,
                fromHistory = true,
                returnToHistory = true,
                platforms = platformsForFusionFormat(format)
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

        composable("filters") {

            FilterScreen(
                navController = navController,
                viewModel = photoViewModel
            )
        }


    }
}

fun platformsForFusionFormat(format: String?): List<PhotoPlatform> {
    return when (format?.normalizedFusionFormat()) {
        "cuadrado", "square", "horizontal" ->
            listOf(PhotoPlatform(key = "facebook", name = "Facebook", iconUrl = null))
        "semivertical" ->
            listOf(
                PhotoPlatform(key = "facebook", name = "Facebook", iconUrl = null),
                PhotoPlatform(key = "instagram", name = "Instagram", iconUrl = null)
            )
        "vertical" ->
            listOf(PhotoPlatform(key = "instagram", name = "Instagram", iconUrl = null))
        else -> emptyList()
    }
}

private fun String.normalizedFusionFormat(): String =
    trim()
        .lowercase()
        .replace("_", "")
        .replace("-", "")
        .replace(" ", "")
