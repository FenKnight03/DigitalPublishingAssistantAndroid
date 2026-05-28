package com.ljdit.digitalpublishing.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.core.ui.FusionActionCenter
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FusionPreviewScreen(
    navController: NavController,
    photoId: String? = null,
    logoId: String? = null,
    coordinate: String? = null,
    fusionId: String? = null,
    fromHistory: Boolean = false,
    viewModel: FusionViewModel = viewModel()
) {
    val preview by viewModel.preview.collectAsState()
    val savedFusionId by viewModel.savedFusionId.collectAsState()
    val isProcessingPreview by viewModel.isProcessing.collectAsState()
    val fusionActionState by FusionActionCenter.state.collectAsState()

    var caption by remember { mutableStateOf("") }
    var captionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(preview?.data?.caption, fromHistory) {
        if (fromHistory) {
            caption = preview?.data?.caption.orEmpty()
        }
    }

    LaunchedEffect(fusionId) {
        if (fromHistory && fusionId != null) {
            viewModel.loadFusionById(fusionId.toInt())
        } else {
            viewModel.generatePreview(
                photoId?.toInt() ?: return@LaunchedEffect,
                logoId?.toInt() ?: return@LaunchedEffect,
                coordinate?.toInt() ?: return@LaunchedEffect
            )
        }
    }

    val calendar = remember { Calendar.getInstance() }
    var scheduledTime by remember { mutableStateOf<Long?>(null) }
    var scheduledDateText by remember { mutableStateOf("") }
    val finalFusionId = if (fromHistory) fusionId?.toIntOrNull() else savedFusionId

    fun captionOrShowError(): String? {
        val cleanCaption = caption.trim()
        return if (cleanCaption.isBlank()) {
            captionError = "El caption es obligatorio."
            null
        } else {
            cleanCaption
        }
    }

    fun navigateToGallery() {
        navController.navigate("home") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF2F4F8)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F4F8))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PreviewCard {
                    Text(
                        text = "Revisa antes de enviar",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Ajusta el caption, programa si hace falta y guarda la fusion para continuar despues.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                PreviewImageCard(base64 = preview?.data?.image)

                PreviewCard {
                    Text(
                        text = "Caption",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = caption,
                        onValueChange = {
                            caption = it
                            captionError = null
                        },
                        label = { Text("Texto de publicacion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        isError = captionError != null,
                        supportingText = {
                            captionError?.let { Text(it) }
                        }
                    )
                }

                PreviewCard {
                    Text(
                        text = "Programacion",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            DatePickerDialog(
                                navController.context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    TimePickerDialog(
                                        navController.context,
                                        { _, hour, minute ->
                                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                                            calendar.set(Calendar.MINUTE, minute)

                                            scheduledTime = calendar.timeInMillis
                                            scheduledDateText = SimpleDateFormat(
                                                "dd/MM/yyyy HH:mm",
                                                Locale.getDefault()
                                            ).format(calendar.time)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (scheduledDateText.isBlank()) {
                                "Seleccionar fecha"
                            } else {
                                scheduledDateText
                            }
                        )
                    }

                    TextButton(
                        onClick = {
                            scheduledTime = null
                            scheduledDateText = ""
                        }
                    ) {
                        Text("Quitar programacion")
                    }
                }

                PreviewCard {
                    val actionsEnabled =
                        !isProcessingPreview && !fusionActionState.isProcessing

                    if (!fromHistory) {
                        Button(
                            onClick = {
                                val cleanCaption = captionOrShowError() ?: return@Button

                                FusionActionCenter.saveFusion(
                                    photoId = photoId!!.toInt(),
                                    logoId = logoId!!.toInt(),
                                    coordinate = coordinate!!.toInt(),
                                    caption = cleanCaption
                                )

                                navigateToGallery()
                            },
                            enabled = actionsEnabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val cleanCaption = captionOrShowError() ?: return@Button

                                FusionActionCenter.saveAndPublish(
                                    photoId = photoId!!.toInt(),
                                    logoId = logoId!!.toInt(),
                                    coordinate = coordinate!!.toInt(),
                                    caption = cleanCaption,
                                    scheduledTime = scheduledTime
                                )

                                navigateToGallery()
                            },
                            enabled = actionsEnabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (scheduledTime == null) {
                                    "Guardar y publicar"
                                } else {
                                    "Guardar y programar"
                                }
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                val cleanCaption = captionOrShowError() ?: return@Button

                                finalFusionId?.let {
                                    FusionActionCenter.publishFusion(
                                        fusionId = it,
                                        caption = cleanCaption,
                                        scheduledTime = scheduledTime
                                    )
                                }

                                navigateToGallery()
                            },
                            enabled = actionsEnabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (scheduledTime == null) {
                                    "Publicar"
                                } else {
                                    "Programar"
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun PreviewImageCard(base64: String?) {
    PreviewCard(contentPadding = 0.dp) {
        if (base64 == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@PreviewCard
        }

        val bitmap = remember(base64) {
            try {
                val cleanBase64 = base64.substringAfter("base64,", base64)
                val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null
            }
        }

        when {
            base64.isEmpty() -> {
                Text("Imagen vacia", modifier = Modifier.padding(16.dp))
            }

            bitmap == null -> {
                Text("Error al decodificar imagen", modifier = Modifier.padding(16.dp))
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(Color(0xFFF2F2F2))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Fusion preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewCard(
    contentPadding: androidx.compose.ui.unit.Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
