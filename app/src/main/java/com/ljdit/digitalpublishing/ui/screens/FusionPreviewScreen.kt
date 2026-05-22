package com.ljdit.digitalpublishing.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FusionPreviewScreen(
    navController: NavController,
    photoId: String? = null,
    distributorId: String? = null,
    coordinate: String? = null,
    fusionId: String? = null,
    fromHistory: Boolean = false,
    viewModel: FusionViewModel = viewModel()
    ) {

        Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(color = White)
            ) {

                val preview by viewModel.preview.collectAsState()
                val savedFusionId by viewModel.savedFusionId.collectAsState()
                val isProcessing by viewModel.isProcessing.collectAsState()
                val processingMessage by viewModel.processingMessage.collectAsState()
                val actionResult by viewModel.actionResult.collectAsState()

                var caption by remember {
                    mutableStateOf("")
                }

                LaunchedEffect(preview?.data?.caption, fromHistory) {
                    if (fromHistory) {
                        caption = preview?.data?.caption.orEmpty()
                    }
                }

                val calendar = remember {
                    Calendar.getInstance()
                }

                var scheduledTime by remember {
                    mutableStateOf<Long?>(null)
                }

                var scheduledDateText by remember {
                    mutableStateOf("")
                }

                // 🔄 Cargar preview
                LaunchedEffect(fusionId) {
                    if (fromHistory && fusionId != null) {
                        viewModel.loadFusionById(fusionId.toInt())
                    } else {
                        viewModel.generatePreview(
                            photoId?.toInt() ?: return@LaunchedEffect,
                            distributorId?.toInt() ?: return@LaunchedEffect,
                            coordinate?.toInt() ?: return@LaunchedEffect
                        )
                    }
                }

                val finalFusionId = if (fromHistory) {
                    fusionId?.toIntOrNull()
                } else {
                    savedFusionId
                }

                Box(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        Text("Preview de fusión")

                        Spacer(modifier = Modifier.height(20.dp))

                        // IMAGEN
                        preview?.let {

                            val base64 = it.data.image

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
                                base64.isNullOrEmpty() -> {
                                    Text("Imagen vacía")
                                }

                                bitmap == null -> {
                                    Text("Error al decodificar imagen")
                                }

                                else -> {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Fusion preview",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                        } ?: CircularProgressIndicator()

                        Spacer(modifier = Modifier.height(16.dp))

                        // CAPTION
                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            label = { Text("Texto de publicación") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {

                                DatePickerDialog(

                                    navController.context,

                                    { _, year, month, dayOfMonth ->

                                        calendar.set(
                                            Calendar.YEAR,
                                            year
                                        )

                                        calendar.set(
                                            Calendar.MONTH,
                                            month
                                        )

                                        calendar.set(
                                            Calendar.DAY_OF_MONTH,
                                            dayOfMonth
                                        )

                                        TimePickerDialog(

                                            navController.context,

                                            { _, hour, minute ->

                                                calendar.set(
                                                    Calendar.HOUR_OF_DAY,
                                                    hour
                                                )

                                                calendar.set(
                                                    Calendar.MINUTE,
                                                    minute
                                                )

                                                scheduledTime =
                                                    calendar.timeInMillis

                                                scheduledDateText =

                                                    SimpleDateFormat(
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

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {

                                scheduledTime = null

                                scheduledDateText = ""
                            }
                        ) {

                            Text("Quitar programación")
                        }


                        if (!fromHistory) {

                            Button(
                                onClick = {
                                    viewModel.saveFusion(
                                        photoId!!.toInt(),
                                        distributorId!!.toInt(),
                                        coordinate!!.toInt(),
                                        caption
                                    )
                                },
                                enabled = !isProcessing,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    viewModel.saveAndPublish(
                                        photoId = photoId!!.toInt(),
                                        distributorId = distributorId!!.toInt(),
                                        coordinate = coordinate!!.toInt(),
                                        caption = caption,
                                        scheduledTime = scheduledTime
                                    )
                                },
                                enabled = !isProcessing,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar y publicar")
                            }

                        } else {

                            Button(
                                onClick = {
                                    finalFusionId?.let {
                                        viewModel.publishFusion(
                                            it,
                                            caption,
                                            scheduledTime
                                        )
                                    }
                                },
                                enabled = !isProcessing,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publicar")
                            }
                        }
                    }

                    // OVERLAY DE PROCESAMIENTO / RESULTADO
                    if (
                        isProcessing
                        || actionResult != null
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .fillMaxHeight(0.7f)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),

                                    verticalArrangement = Arrangement.Center,

                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    if (isProcessing) {

                                        CircularProgressIndicator()

                                        Spacer(
                                            modifier = Modifier.height(24.dp)
                                        )

                                        Text(
                                            text = processingMessage
                                                ?: "Procesando..."
                                        )

                                    } else {

                                        Text(
                                            text = actionResult ?: "",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Spacer(
                                            modifier = Modifier.height(32.dp)
                                        )

                                        Button(

                                            onClick = {

                                                val shouldNavigateToHistory =
                                                    actionResult
                                                        ?.contains(
                                                            "correctamente",
                                                            ignoreCase = true
                                                        ) == true

                                                viewModel.clearActionResult()

                                                if (shouldNavigateToHistory) {

                                                    navController.navigate(
                                                        "home/1"
                                                    ) {

                                                        popUpTo("home") {
                                                            inclusive = true
                                                        }

                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        ) {

                                            Text("OK")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

}
