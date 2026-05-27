package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

private val HistoryBrand = Color(0xFFD1143A)
private val HistoryInk = Color(0xFF141418)
private val HistorySoftInk = Color(0xFF5D6675)
private val HistoryField = Color(0xFFF3F3F6)

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
            val normalized = value.replace("Z", "+00:00")

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
                    } ?: throw DateTimeParseException("Unsupported date", normalized, 0)
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isActionable) 1f else 0.76f)
            .then(
                if (isActionable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(HistoryField),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fusion.thumbnail_url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = fusion.producto_nombre ?: "Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HistoryInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = fusion.distributor_name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HistorySoftInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StatusPill(
                    text = fusion.formato ?: "N/A",
                    tint = HistorySoftInk
                )

                Text(
                    text = fusion.fecha_publicacion.formattedPublicationDate(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = HistorySoftInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isActionable) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = HistorySoftInk.copy(alpha = 0.55f)
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Rectangle,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = tint
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = tint,
            maxLines = 1
        )
    }
}
