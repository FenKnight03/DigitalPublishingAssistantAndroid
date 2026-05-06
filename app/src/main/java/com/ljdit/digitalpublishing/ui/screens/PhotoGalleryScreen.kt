package com.ljdit.digitalpublishing.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ljdit.digitalpublishing.model.Photo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    Column {

        Button(
            onClick = { navController.navigate("fusion_history") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Mis imagenes")
        }

        val photos = viewModel.photos.collectAsState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {

            items(photos.value) { photo ->

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