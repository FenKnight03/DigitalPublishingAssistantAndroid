package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.ui.FusionActionCenter
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.model.PhotoFilters
import com.ljdit.digitalpublishing.ui.components.PhotoCard
import com.ljdit.digitalpublishing.viewmodel.PhotoViewModel

private val GalleryBrand = Color(0xFFD1143A)
private val GalleryInk = Color(0xFF141418)
private val GallerySoftInk = Color(0xFF5D6675)
private val GalleryCanvas = Color(0xFFF2F4F8)
private val GalleryElevated = Color.White
private val GalleryField = Color(0xFFF3F3F6)

@Composable
fun PhotoGalleryScreen(
    navController: NavController,
    viewModel: PhotoViewModel = viewModel(),
    applyStatusBarPadding: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val suggestions by viewModel.searchSuggestions.collectAsState()
    val fusionActionState by FusionActionCenter.state.collectAsState()
    var isShowingFilters by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GalleryCanvas
    ) { innerPadding ->
        fusionActionState.result?.let { result ->
            AlertDialog(
                onDismissRequest = { FusionActionCenter.clearResult() },
                title = { Text("Resultado") },
                text = { Text(result) },
                confirmButton = {
                    TextButton(onClick = { FusionActionCenter.clearResult() }) {
                        Text("OK")
                    }
                }
            )
        }

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
                    onLogoutClick = { SessionManager.logout(context) }
                )
            }

            item {
                GallerySummary(
                    totalPhotos = allPhotos.size,
                    visiblePhotos = photos.size,
                    activeFilters = filters.activeCount()
                )
            }

            item {
                FilterHeader(
                    visiblePhotos = photos.size,
                    activeFilters = filters.activeCount(),
                    isShowingFilters = isShowingFilters,
                    onClick = { isShowingFilters = !isShowingFilters }
                )
            }

            if (isShowingFilters) {
                item {
                    GalleryFilterPanel(
                        filters = filters,
                        suggestions = suggestions,
                        availableOrigins = allPhotos.availableOrigins(),
                        onSearchChanged = viewModel::updateSearchSuggestions,
                        onApply = { nextFilters ->
                            viewModel.applyFilters(nextFilters)
                            isShowingFilters = false
                        },
                        onClear = {
                            viewModel.applyFilters(PhotoFilters())
                            viewModel.updateSearchSuggestions("")
                            isShowingFilters = false
                        }
                    )
                }
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
private fun GallerySummary(
    totalPhotos: Int,
    visiblePhotos: Int,
    activeFilters: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GalleryElevated,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
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

                if (activeFilters > 0) {
                    StatusPill(
                        text = "$visiblePhotos visibles",
                        icon = Icons.Rounded.Check,
                        tint = GalleryBrand
                    )
                }
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
private fun FilterHeader(
    visiblePhotos: Int,
    activeFilters: Int,
    isShowingFilters: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onClick,
            color = GalleryElevated,
            shape = RoundedCornerShape(50),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = null,
                    tint = GalleryBrand
                )

                Text(
                    text = if (isShowingFilters) "Ocultar filtros" else "Filtros",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = GalleryInk
                )
            }
        }

        if (activeFilters > 0) {
            Spacer(modifier = Modifier.width(10.dp))
            StatusPill(
                text = "$activeFilters activos",
                icon = Icons.Rounded.Check,
                tint = GalleryBrand
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$visiblePhotos fotos",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = GallerySoftInk
        )
    }
}

@Composable
private fun GalleryFilterPanel(
    filters: PhotoFilters,
    suggestions: List<String>,
    availableOrigins: List<String>,
    onSearchChanged: (String) -> Unit,
    onApply: (PhotoFilters) -> Unit,
    onClear: () -> Unit
) {
    val formatos = remember(filters.formatos) {
        mutableStateListOf<String>().apply { addAll(filters.formatos) }
    }
    val origenes = remember(filters.origenes, availableOrigins) {
        mutableStateListOf<String>().apply {
            addAll(filters.origenes.filter { it in availableOrigins })
        }
    }
    val contenidos = remember(filters.contenidos) {
        mutableStateListOf<String>().apply { addAll(filters.contenidos) }
    }
    var orden by remember(filters.orden) { mutableStateOf(filters.orden) }
    var busqueda by remember(filters.busqueda) { mutableStateOf(filters.busqueda) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GalleryElevated,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            FilterTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                    onSearchChanged(it)
                },
                suggestions = suggestions,
                onSuggestionClick = {
                    busqueda = it
                    onSearchChanged("")
                }
            )

            FilterGroup(title = "Formato") {
                listOf(
                    "horizontal" to "Horizontal",
                    "cuadrado" to "Cuadrado",
                    "semivertical" to "Semivertical",
                    "vertical" to "Vertical"
                ).forEach { (value, label) ->
                    FilterCheckbox(
                        label = label,
                        checked = formatos.contains(value),
                        onCheckedChange = { checked ->
                            if (checked) formatos.add(value) else formatos.remove(value)
                        }
                    )
                }
            }

            FilterGroup(title = "Contenido") {
                listOf(
                    "solo" to "Solo",
                    "en_uso" to "En uso"
                ).forEach { (value, label) ->
                    FilterCheckbox(
                        label = label,
                        checked = contenidos.contains(value),
                        onCheckedChange = { checked ->
                            if (checked) contenidos.add(value) else contenidos.remove(value)
                        }
                    )
                }
            }

            FilterGroup(title = "Orden") {
                FilterRadio(
                    label = "Mas recientes",
                    selected = orden == "recientes",
                    onClick = { orden = "recientes" }
                )
                FilterRadio(
                    label = "Mas antiguas",
                    selected = orden == "antiguas",
                    onClick = { orden = "antiguas" }
                )
            }

            FilterGroup(title = "Origen") {
                if (availableOrigins.isEmpty()) {
                    Text(
                        text = "Sin paises disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GallerySoftInk
                    )
                } else {
                    availableOrigins.forEach { value ->
                        FilterCheckbox(
                            label = value.replaceFirstChar { it.titlecase() },
                            checked = origenes.contains(value),
                            onCheckedChange = { checked ->
                                if (checked) origenes.add(value) else origenes.remove(value)
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onApply(
                        PhotoFilters(
                            formatos = formatos.toSet(),
                            origenes = origenes.toSet(),
                            contenidos = contenidos.toSet(),
                            orden = orden,
                            busqueda = busqueda
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GalleryBrand,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Aplicar filtros")
            }

            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GalleryBrand
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Limpiar filtros")
            }
        }
    }
}

@Composable
private fun FilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = GalleryBrand
                )
            },
            label = { Text("Buscar producto") },
            placeholder = { Text("Ej: B420H") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GalleryBrand.copy(alpha = 0.45f),
                focusedLabelColor = GalleryBrand,
                cursorColor = GalleryBrand,
                focusedContainerColor = GalleryField,
                unfocusedContainerColor = GalleryField,
                unfocusedBorderColor = Color.Transparent
            )
        )

        if (suggestions.isNotEmpty() && value.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                suggestions.forEach { suggestion ->
                    OutlinedButton(
                        onClick = { onSuggestionClick(suggestion) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GalleryBrand
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(suggestion)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = GallerySoftInk
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GalleryField, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            content = content
        )
    }
}

@Composable
private fun FilterCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = GalleryBrand,
                checkmarkColor = Color.White
            )
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = GalleryInk
        )
    }
}

@Composable
private fun FilterRadio(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = GalleryBrand
            )
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = GalleryInk
        )
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

private fun PhotoFilters.activeCount(): Int {
    var count = 0
    if (formatos.isNotEmpty()) count++
    if (origenes.isNotEmpty()) count++
    if (contenidos.isNotEmpty()) count++
    if (orden != "recientes") count++
    if (busqueda.isNotBlank()) count++
    return count
}

private fun List<Photo>.availableOrigins(): List<String> {
    return mapNotNull { photo ->
        photo.origen
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }
    }
        .distinct()
        .sorted()
}
