package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ljdit.digitalpublishing.model.FusionItem


@Composable
fun FusionItemView(
    fusion: FusionItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() } // 👈 clave
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            AsyncImage(
                model = fusion.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Text("Foto ID: ${fusion.photo_id}")
            Text("Distribuidor: ${fusion.distributor_name}")
            Text("Coordenada: ${fusion.coordenada}")

            fusion.fecha_publicacion?.let {
                Text("Fecha: $it")
            } ?: Text("Sin fecha")
        }
    }
}