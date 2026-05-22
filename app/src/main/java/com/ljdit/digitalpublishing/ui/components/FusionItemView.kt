package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ljdit.digitalpublishing.model.FusionItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val displayDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-MX"))

private val serverDateFormatters: List<DateTimeFormatter> =
    listOf(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    )

private fun String?.formattedPublicationDate(): String {
    val value = this?.trim()

    if (value.isNullOrBlank()) {
        return "Sin fecha"
    }

    val zone = ZoneId.systemDefault()

    return try {
        val millis = value.toLongOrNull()

        if (millis != null) {
            val instant =
                if (millis > 9_999_999_999L) {
                    Instant.ofEpochMilli(millis)
                } else {
                    Instant.ofEpochSecond(millis)
                }

            displayDateFormatter.format(instant.atZone(zone))
        } else {
            val normalized =
                value.replace("Z", "+00:00")

            val dateTime =
                try {
                    OffsetDateTime.parse(normalized)
                        .atZoneSameInstant(zone)
                        .toLocalDateTime()
                } catch (_: DateTimeParseException) {
                    serverDateFormatters.firstNotNullOfOrNull { formatter ->
                        try {
                            LocalDateTime.parse(normalized, formatter)
                        } catch (_: DateTimeParseException) {
                            null
                        }
                    } ?: throw DateTimeParseException(
                        "Unsupported date",
                        normalized,
                        0
                    )
                }

            displayDateFormatter.format(dateTime)
        }
    } catch (_: Exception) {
        value
    }
}

@Composable
fun FusionItemView(
    fusion: FusionItem,
    isActionable: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .alpha(if (isActionable) 1f else 0.72f)
            .then(
                if (isActionable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
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
                Text(
                    text = fusion.producto_nombre ?: "Producto",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Distribuidor: ${fusion.distributor_name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Formato: ${fusion.formato ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = fusion.fecha_publicacion.formattedPublicationDate(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
