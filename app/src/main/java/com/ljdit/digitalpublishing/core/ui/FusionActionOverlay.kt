package com.ljdit.digitalpublishing.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val OverlayBrand = Color(0xFFD1143A)
private val OverlayInk = Color(0xFF141418)

@Composable
fun FusionActionOverlay() {
    val fusionActionState by FusionActionCenter.state.collectAsState()

    if (fusionActionState.isProcessing) {
        PublishingActivityBanner(
            title = fusionActionState.title ?: "Procesando",
            message = fusionActionState.message ?: "Puedes seguir navegando."
        )
    }

    if (fusionActionState.resultTitle != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(fusionActionState.resultTitle.orEmpty()) },
            text = { Text(fusionActionState.resultMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { FusionActionCenter.dismissResult() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun PublishingActivityBanner(
    title: String,
    message: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 30.dp),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                color = OverlayBrand,
                strokeWidth = 3.dp
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OverlayInk
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = OverlayInk.copy(alpha = 0.62f)
                )
            }
        }
    }
}
