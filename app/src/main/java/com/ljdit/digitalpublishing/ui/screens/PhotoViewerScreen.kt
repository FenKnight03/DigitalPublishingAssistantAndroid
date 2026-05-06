package com.ljdit.digitalpublishing.ui.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.model.PhotoCoordinate
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel
import com.ljdit.digitalpublishing.viewmodel.PhotoViewerViewModel

private fun PhotoCoordinate.xFraction(photo: Photo): Float? {
    return when {
        x in 0f..1f -> x
        photo.width != null && photo.width > 0f -> x / photo.width
        else -> null
    }?.coerceIn(0f, 1f)
}

private fun PhotoCoordinate.yFraction(photo: Photo): Float? {
    return when {
        y in 0f..1f -> y
        photo.height != null && photo.height > 0f -> y / photo.height
        else -> null
    }?.coerceIn(0f, 1f)
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PhotoViewerScreen(
    navController: NavController,
    photoId: String?,
    photoViewModel: PhotoViewModel = viewModel(),
    viewerViewModel: PhotoViewerViewModel = viewModel()
) {

    val photos by photoViewModel.photos.collectAsState()

    val photo = photos.find {
        it.id.toString() == photoId
    }

    LaunchedEffect(photo) {

        photo?.let {
            viewerViewModel.setPhoto(it)
        }
    }

    val distributors by viewerViewModel.distributors.collectAsState()
    val selectedCoordinate by viewerViewModel.selectedCoordinate.collectAsState()
    val selectedDistributorId by viewerViewModel.selectedDistributorId.collectAsState()
    val preview by viewerViewModel.preview.collectAsState()
    val isLoading by viewerViewModel.isLoading.collectAsState()

    LaunchedEffect(photoId, photos.isEmpty()) {
        if (photoId != null && photos.isEmpty()) {
            photoViewModel.loadPhotos()
        }
    }

    // Inicializar
    LaunchedEffect(photoId) {

        if (photoId != null) {

            if (photos.isEmpty()) {
                photoViewModel.loadPhotos()
            }

            viewerViewModel.loadDistributors()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Photo Viewer",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentAlignment = Alignment.Center
        ) {

            val base64 = preview?.data?.image

            val bitmap = remember(base64) {
                try {

                    if (base64.isNullOrEmpty()) return@remember null

                    val cleanBase64 =
                        base64.substringAfter("base64,", base64)

                    val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

            } else {

                photo?.let {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            val availableCoordinates = photo?.coordinates.orEmpty()
            val imageAspectRatio = photo?.let {
                val width = it.width
                val height = it.height

                if (width != null && height != null && width > 0f && height > 0f) {
                    width / height
                } else {
                    null
                }
            }
            val containerAspectRatio = maxWidth.value / maxHeight.value
            val displayedImageWidth =
                if (imageAspectRatio != null && imageAspectRatio < containerAspectRatio) {
                    maxHeight * imageAspectRatio
                } else {
                    maxWidth
                }
            val displayedImageHeight =
                if (imageAspectRatio != null && imageAspectRatio > containerAspectRatio) {
                    maxWidth / imageAspectRatio
                } else {
                    maxHeight
                }
            val imageOffsetX = (maxWidth - displayedImageWidth) / 2
            val imageOffsetY = (maxHeight - displayedImageHeight) / 2

            availableCoordinates.forEach { coordinate ->
                val xFraction = photo?.let { coordinate.xFraction(it) }
                val yFraction = photo?.let { coordinate.yFraction(it) }
                if (xFraction == null || yFraction == null) {
                    return@forEach
                }

                val markerSize = 24.dp

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = imageOffsetX + (displayedImageWidth * xFraction) - (markerSize / 2),
                            y = imageOffsetY + (displayedImageHeight * yFraction) - (markerSize / 2)
                        )
                        .size(markerSize)
                        .clip(CircleShape)
                        .background(
                            if (selectedCoordinate == coordinate.id)
                                Color.Green
                            else
                                Color.Red
                        )
                        .clickable {
                            viewerViewModel.selectCoordinate(coordinate.id)
                        }
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Distribuidores
        Text(
            text = "Selecciona distribuidor",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(distributors) { distributor ->

                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .clickable {
                            viewerViewModel.selectDistributor(distributor.id)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if (selectedDistributorId == distributor.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AsyncImage(
                            model = distributor.logoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = distributor.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // aplicar cambios
        Button(
            onClick = {
                viewerViewModel.applyFusion()
            },
            enabled =
            selectedDistributorId != null &&
                    selectedCoordinate != null &&
                    !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Aplicar cambios")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ir a preview
        Button(
            onClick = {

                navController.navigate(
                    "preview/${photo?.id}/${selectedDistributorId}/${selectedCoordinate}"
                )
            },
            enabled = preview?.ok == true && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Vista previa")
        }
    }
}
