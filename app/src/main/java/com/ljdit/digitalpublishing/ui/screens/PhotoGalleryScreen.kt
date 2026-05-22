package com.ljdit.digitalpublishing.ui.screens

import android.R
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.ljdit.digitalpublishing.model.Photo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.ui.components.PhotoCard
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

@Composable
fun PhotoGalleryScreen(
    navController: NavController,
    viewModel: PhotoViewModel = viewModel()
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

            LaunchedEffect(Unit) {
                viewModel.loadPhotos()
            }

            val photos by
                viewModel.photos.collectAsState()

            Column {

                Button(
                    onClick = { navController.navigate("fusion_history") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Mis imagenes")
                }


                Button(
                    onClick = {
                        navController.navigate("filters")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {

                    Text("Filtros")
                }

                LazyVerticalGrid(
                    GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier.fillMaxSize(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    items(

                        items = photos,

                        key = { photo ->
                            photo.id
                        },

                        contentType = {
                            "photo"
                        }

                    ) { photo ->

                        PhotoCard(
                            photo = photo,
                            onClick = {
                                navController.navigate(
                                    "viewer/${photo.id}"
                                )
                            }
                        )

                    }

                }
            }
        }
    }
}