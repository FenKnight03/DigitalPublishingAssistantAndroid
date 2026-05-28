package com.ljdit.digitalpublishing.ui.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Rectangle
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.ljdit.digitalpublishing.model.Distributor
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.model.PhotoCoordinate
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel
import com.ljdit.digitalpublishing.viewmodel.PhotoViewerViewModel

private val ViewerBrand = Color(0xFFD1143A)
private val ViewerPositive = Color(0xFF148C45)
private val ViewerInk = Color(0xFF141418)
private val ViewerSoftInk = Color(0xFF5D6675)
private val ViewerCanvas = Color(0xFFF2F4F8)
private val ViewerElevated = Color.White
private val ViewerField = Color(0xFFF3F3F6)

private fun PhotoCoordinate.xFraction(sourceWidth: Int?): Float? {
    return when {
        x in 0f..1f -> x
        sourceWidth != null && sourceWidth > 0 ->
            x / sourceWidth.toFloat()
        else -> null
    }?.coerceIn(0f, 1f)
}

private fun PhotoCoordinate.yFraction(sourceHeight: Int?): Float? {
    return when {
        y in 0f..1f -> y
        sourceHeight != null && sourceHeight > 0 ->
            y / sourceHeight.toFloat()
        else -> null
    }?.coerceIn(0f, 1f)
}

private data class LogoSelectionItem(
    val logoId: Int,
    val imageUrl: String,
    val label: String
)

private fun Distributor.logoSelectionItems(): List<LogoSelectionItem> {
    val expandedLogos = logos.mapIndexed { index, logo ->
        LogoSelectionItem(
            logoId = logo.id,
            imageUrl = logo.imageUrl,
            label = if (logos.size > 1) "${name} ${index + 1}" else name
        )
    }

    if (expandedLogos.isNotEmpty()) {
        return expandedLogos
    }

    return logoId?.let {
        listOf(
            LogoSelectionItem(
                logoId = it,
                imageUrl = logoUrl,
                label = name
            )
        )
    } ?: emptyList()
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PhotoViewerScreen(
    navController: NavController,
    photoId: String?,
    photoViewModel: PhotoViewModel = viewModel(),
    viewerViewModel: PhotoViewerViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ViewerCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxSize()
                .background(ViewerCanvas)
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
            val selectedLogoId by viewerViewModel.selectedLogoId.collectAsState()
            val preview by viewerViewModel.preview.collectAsState()
            val isLoading by viewerViewModel.isLoading.collectAsState()

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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (photo == null) {
                    ViewerCard {
                        Text(
                            text = "No se encontro la imagen seleccionada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ViewerBrand
                        )
                    }
                    return@Column
                }

                HeaderSection(photo)

                ImageViewerCard(
                    photo = photo,
                    previewBase64 = preview?.data?.image,
                    selectedCoordinate = selectedCoordinate,
                    selectedLogoId = selectedLogoId,
                    isLoading = isLoading,
                    onCoordinateClick = { coordinateId ->
                        viewerViewModel.selectCoordinate(coordinateId)

                        if (selectedLogoId != null) {
                            viewerViewModel.applyFusion()
                        }
                    }
                )

                DistributorSection(
                    logoItems = distributors.flatMap { it.logoSelectionItems() },
                    selectedLogoId = selectedLogoId,
                    isLoading = isLoading,
                    onLogoClick = { logoId ->
                        viewerViewModel.selectLogo(logoId)
                    }
                )

                PositionSection(
                    selectedLogoId = selectedLogoId,
                    selectedCoordinate = selectedCoordinate,
                    isLoading = isLoading,
                    hasCoordinates = photo.coordinates.orEmpty().isNotEmpty()
                )

                Button(
                    onClick = {
                        navController.navigate(
                            "preview/${photo.id}/${selectedLogoId}/${selectedCoordinate}"
                        )
                    },
                    enabled = preview?.ok == true && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ViewerBrand,
                        contentColor = White,
                        disabledContainerColor = Color(0xFFB7B7BE),
                        disabledContentColor = White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir preview")
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(photo: Photo) {
    ViewerCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionEyebrow("Composicion", Icons.Rounded.AutoFixHigh)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = photo.producto?.takeIf { it.isNotBlank() } ?: "Imagen para publicar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ViewerInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Elige el logo y toca una posicion disponible para generar la fusion.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ViewerSoftInk
                    )
                }

                StatusBadge(
                    text = photo.formatTitle(),
                    icon = Icons.Rounded.Rectangle,
                    tint = ViewerBrand
                )
            }
        }
    }
}

@Composable
private fun ImageViewerCard(
    photo: Photo,
    previewBase64: String?,
    selectedCoordinate: Int?,
    selectedLogoId: Int?,
    isLoading: Boolean,
    onCoordinateClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ViewerElevated,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 8.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            contentAlignment = Alignment.Center
        ) {
            var loadedImageSize by remember(photo.id) {
                mutableStateOf<Pair<Int, Int>?>(null)
            }

            val bitmap = remember(previewBase64) {
                try {
                    if (previewBase64.isNullOrEmpty()) return@remember null

                    val cleanBase64 =
                        previewBase64.substringAfter("base64,", previewBase64)

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
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.imageUrl)
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .listener(
                            onSuccess = { _, result ->
                                val drawable = result.drawable
                                var width = drawable.intrinsicWidth
                                var height = drawable.intrinsicHeight

                                if (width <= 0 || height <= 0) {
                                    val imageBitmap = drawable.toBitmap()
                                    width = imageBitmap.width
                                    height = imageBitmap.height
                                }

                                if (width > 0 && height > 0) {
                                    loadedImageSize = width to height
                                }
                            }
                        )
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            val availableCoordinates = photo.coordinates.orEmpty()
            val sourceImageSize =
                bitmap?.let { it.width to it.height }
                    ?: photo.knownImageSize()
                    ?: loadedImageSize

            val imageAspectRatio = sourceImageSize?.let { (width, height) ->
                if (width > 0 && height > 0) {
                    width.toFloat() / height.toFloat()
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
                val xFraction = coordinate.xFraction(sourceImageSize?.first)
                val yFraction = coordinate.yFraction(sourceImageSize?.second)
                if (xFraction == null || yFraction == null) {
                    return@forEach
                }

                val markerSize = 28.dp
                val isSelected = selectedCoordinate == coordinate.id

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
                            if (isSelected) {
                                Color.Transparent
                            } else {
                                ViewerBrand
                            }
                        )
                        .clickable {
                            onCoordinateClick(coordinate.id)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isSelected) {
                        Text(
                            text = coordinate.id.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }

            ImageCaption(
                text = if (bitmap == null) "Original" else "Fusion generada",
                icon = if (bitmap == null) Icons.Rounded.Image else Icons.Rounded.AutoFixHigh,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ViewerBrand
                )
            }
        }
    }
}

@Composable
private fun DistributorSection(
    logoItems: List<LogoSelectionItem>,
    selectedLogoId: Int?,
    isLoading: Boolean,
    onLogoClick: (Int) -> Unit
) {
    ViewerCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionEyebrow("Distribuidor", Icons.Rounded.Business)
                Spacer(modifier = Modifier.weight(1f))

                logoItems.firstOrNull { it.logoId == selectedLogoId }?.let { selected ->
                    StatusBadge(
                        text = selected.label,
                        icon = Icons.Rounded.Check,
                        tint = ViewerPositive
                    )
                }
            }

            if (isLoading && logoItems.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = ViewerBrand
                    )
                    Text(
                        text = "Cargando distribuidores...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ViewerSoftInk
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(logoItems) { logoItem ->
                        LogoTile(
                            logoItem = logoItem,
                            isSelected = selectedLogoId == logoItem.logoId,
                            onClick = { onLogoClick(logoItem.logoId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionSection(
    selectedLogoId: Int?,
    selectedCoordinate: Int?,
    isLoading: Boolean,
    hasCoordinates: Boolean
) {
    ViewerCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionEyebrow("Posicion", Icons.Rounded.Place)

            val message = when {
                selectedLogoId == null ->
                    "Selecciona un logotipo para preparar la composicion."
                isLoading ->
                    "Generando composicion..."
                !hasCoordinates ->
                    "No hay posiciones disponibles para esta imagen."
                selectedCoordinate != null ->
                    "Posicion P$selectedCoordinate seleccionada. Ya puedes abrir el preview."
                else ->
                    "Toca un punto sobre la imagen para elegir donde quedara el logo."
            }

            val tint = when {
                selectedCoordinate != null -> ViewerPositive
                selectedLogoId == null -> ViewerSoftInk
                else -> ViewerBrand
            }

            MessageRow(
                text = message,
                icon = if (selectedLogoId == null) Icons.Rounded.TouchApp else Icons.Rounded.Place,
                tint = tint
            )
        }
    }
}

@Composable
private fun ViewerCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ViewerElevated,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SectionEyebrow(
    text: String,
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
            tint = ViewerSoftInk
        )

        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ViewerSoftInk
        )
    }
}

@Composable
private fun StatusBadge(
    text: String,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .widthIn(max = 140.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
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
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LogoTile(
    logoItem: LogoSelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(106.dp)
            .clickable { onClick() },
        color = if (isSelected) ViewerBrand.copy(alpha = 0.08f) else Color.Transparent,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.dp,
            color = if (isSelected) ViewerBrand else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) ViewerBrand.copy(alpha = 0.10f) else ViewerField),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = logoItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = logoItem.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ViewerInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MessageRow(
    text: String,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tint.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = tint,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ImageCaption(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = White
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
    }
}

private fun Photo.knownImageSize(): Pair<Int, Int>? {
    val widthValue = width
    val heightValue = height
    return if (widthValue != null && heightValue != null && widthValue > 0 && heightValue > 0) {
        widthValue to heightValue
    } else {
        null
    }
}

private fun Photo.formatTitle(): String {
    return when (formato?.lowercase()) {
        "horizontal" -> "Horizontal"
        "cuadrado", "square" -> "Cuadrado"
        "semivertical", "semi_vertical", "semi-vertical" -> "Semivertical"
        "vertical" -> "Vertical"
        else -> "Sin formato"
    }
}
