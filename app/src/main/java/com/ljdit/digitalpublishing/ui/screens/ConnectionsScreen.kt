package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ljdit.digitalpublishing.model.ConnectionStatus
import com.ljdit.digitalpublishing.viewmodel.ConnectionsViewModel

@Composable
fun ConnectionsScreen(
    viewModel: ConnectionsViewModel = viewModel()
) {
    val status by viewModel.status.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Conexiones",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Estado informativo de las integraciones de Meta.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )

        Button(
            onClick = { viewModel.loadStatus() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar estado")
        }

        if (isLoading && status == null) {
            Row(
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text("Revisando conexion...")
            }
        } else {
            status?.let {
                ConnectionSummaryCard(
                    status = it,
                    modifier = Modifier.padding(top = 16.dp)
                )

                ConnectionStatusRow(
                    title = "Token de Meta",
                    subtitle =
                        if (it.metaTokenConfigured) {
                            "Configurado para esta cuenta"
                        } else {
                            "No hay token activo configurado"
                        },
                    isConnected = it.metaTokenConfigured
                )

                ConnectionStatusRow(
                    title = "Facebook",
                    subtitle =
                        it.facebookPageId
                            ?.let { id -> "Pagina conectada: $id" }
                            ?: "Sin pagina conectada",
                    isConnected = it.facebookConnected
                )

                ConnectionStatusRow(
                    title = "Instagram Business",
                    subtitle =
                        it.instagramUserId
                            ?.let { id -> "Cuenta conectada: $id" }
                            ?: "Sin cuenta Business conectada",
                    isConnected = it.instagramConnected
                )

                it.message
                    ?.takeIf { message -> message.isNotBlank() }
                    ?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
            } ?: errorMessage?.let {
                ConnectionStatusRow(
                    title = "No se pudo consultar Meta",
                    subtitle = it,
                    isConnected = false,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectionSummaryCard(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =
                    if (status.isConnected) {
                        "Listo para publicar"
                    } else {
                        "Requiere atencion en web"
                    },
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    if (status.isConnected) {
                        "Meta esta conectado para publicar contenido."
                    } else {
                        "La conexion se administra desde la version web."
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ConnectionStatusRow(
    title: String,
    subtitle: String,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val indicatorColor =
                if (isConnected) {
                    Color(0xFF2E7D32)
                } else {
                    Color(0xFFF9A825)
                }

            Spacer(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
