package com.ljdit.digitalpublishing.ui.screens

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
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.ui.components.PhotoCard
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

private val GalleryBrand = Color(0xFF1F65D6)
private val GalleryInk = Color(0xFF141418)
private val GallerySoftInk = Color(0xFF5D6675)
private val GalleryCanvas = Color(0xFFF2F4F8)
private val GalleryElevated = Color.White
private val GalleryField = Color(0xFFEAF0FA)

@Composable
fun PhotoGalleryScreen(
    navController: NavController,
    viewModel: PhotoViewModel = viewModel(),
    applyStatusBarPadding: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val photos by viewModel.photos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GalleryCanvas
    ) { innerPadding ->
        LazyColumn(
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
                .background(GalleryCanvas),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                top = 10.dp,
                end = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                GalleryTopBar(
                    onFiltersClick = { navController.navigate("filters") },
                    onLogoutClick = { SessionManager.logout(context) }
                )
            }

            item {
                GallerySummary(totalPhotos = photos.size)
            }

            if (photos.isEmpty()) {
                item {
                    EmptyGalleryView()
                }
            } else {
                items(
                    items = photos.groupByFormat(),
                    key = { it.format.key }
                ) { section ->
                    PhotoFormatSection(
                        section = section,
                        onPhotoClick = { photo ->
                            navController.navigate("viewer/${photo.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryTopBar(
    onFiltersClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Fotos",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = GalleryInk
            )

            Text(
                text = "Galer\u00eda por formato",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = GallerySoftInk
            )
        }

        GalleryIconButton(
            icon = Icons.Rounded.Tune,
            contentDescription = "Filtros",
            onClick = onFiltersClick
        )

        Spacer(modifier = Modifier.width(8.dp))

        GalleryIconButton(
            icon = Icons.Rounded.Logout,
            contentDescription = "Cerrar sesi\u00f3n",
            onClick = onLogoutClick
        )
    }
}

@Composable
private fun GalleryIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = GalleryElevated,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(GalleryElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = GalleryBrand
            )
        }
    }
}

@Composable
private fun GallerySummary(totalPhotos: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GalleryElevated,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = "Biblioteca lista",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GalleryInk
                )

                Text(
                    text = "Selecciona una imagen y prueba logos con posiciones disponibles antes de publicar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GallerySoftInk
                )

                StatusPill(
                    text = "$totalPhotos totales",
                    icon = Icons.Rounded.Collections,
                    tint = GallerySoftInk
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GalleryBrand.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Collections,
                    contentDescription = null,
                    tint = GalleryBrand
                )
            }
        }
    }
}

@Composable
private fun PhotoFormatSection(
    section: PhotoSection,
    onPhotoClick: (Photo) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.format.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalleryInk
                )

                Text(
                    text = "${section.photos.size} im\u00e1genes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GallerySoftInk
                )
            }
        }

        if (section.format == GalleryPhotoFormat.Horizontal) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                section.photos.forEach { photo ->
                    PhotoCard(
                        photo = photo,
                        aspectRatio = section.format.aspectRatio,
                        onClick = { onPhotoClick(photo) }
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                section.photos.chunked(2).forEach { rowPhotos ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        rowPhotos.forEach { photo ->
                            Box(modifier = Modifier.weight(1f)) {
                                PhotoCard(
                                    photo = photo,
                                    aspectRatio = section.format.aspectRatio,
                                    onClick = { onPhotoClick(photo) }
                                )
                            }
                        }

                        if (rowPhotos.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .background(GalleryField, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = tint
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = tint
        )
    }
}

@Composable
private fun EmptyGalleryView() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GalleryElevated,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Collections,
                contentDescription = null,
                tint = GalleryBrand,
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = "Sin fotos para mostrar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GalleryInk
            )

            Text(
                text = "Cuando haya resultados disponibles aparecer\u00e1n separados por formato.",
                style = MaterialTheme.typography.bodyMedium,
                color = GallerySoftInk
            )
        }
    }
}

private data class PhotoSection(
    val format: GalleryPhotoFormat,
    val photos: List<Photo>
)

private enum class GalleryPhotoFormat(
    val key: String,
    val title: String,
    val aspectRatio: Float
) {
    Horizontal("horizontal", "Horizontales", 16f / 9f),
    Square("square", "Cuadradas", 1f),
    SemiVertical("semi_vertical", "Semiverticales", 4f / 5f),
    Vertical("vertical", "Verticales", 3f / 4f),
    Unknown("unknown", "Sin formato", 1f)
}

private fun List<Photo>.groupByFormat(): List<PhotoSection> {
    return GalleryPhotoFormat.entries.mapNotNull { format ->
        val photos = filter { it.galleryFormat() == format }
        if (photos.isEmpty()) null else PhotoSection(format, photos)
    }
}

private fun Photo.galleryFormat(): GalleryPhotoFormat {
    when (formato?.lowercase()) {
        "horizontal" -> return GalleryPhotoFormat.Horizontal
        "cuadrado", "square" -> return GalleryPhotoFormat.Square
        "semivertical", "semi_vertical", "semi-vertical" -> return GalleryPhotoFormat.SemiVertical
        "vertical" -> return GalleryPhotoFormat.Vertical
    }

    val widthValue = width
    val heightValue = height

    if (widthValue == null || heightValue == null || heightValue <= 0) {
        return GalleryPhotoFormat.Unknown
    }

    val ratio = widthValue.toFloat() / heightValue.toFloat()

    return when {
        ratio >= 1.15f -> GalleryPhotoFormat.Horizontal
        ratio >= 0.92f -> GalleryPhotoFormat.Square
        ratio >= 0.72f -> GalleryPhotoFormat.SemiVertical
        else -> GalleryPhotoFormat.Vertical
    }
}
