package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ljdit.digitalpublishing.model.FusionItem


@Composable
fun FusionItemView(
    fusion: FusionItem,
    onClick: () -> Unit
) {

    val imageUrl = "https://ljdit.com${fusion.thumbnail_url}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            // 🖼️ Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fusion.thumbnail_url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // 🧠 Producto (asumiendo que lo tienes en el modelo)
                Text(
                    text = fusion.producto_nombre ?: "Producto",
                    style = MaterialTheme.typography.titleMedium
                )

                // 🏢 Distribuidor
                Text(
                    text = "Distribuidor: ${fusion.distributor_name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // 🖼️ Formato
                Text(
                    text = "Formato: ${fusion.formato ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )

                // 📅 Fecha
                Text(
                    text = fusion.fecha_publicacion ?: "Sin fecha",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}