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

    val preview by viewModel.preview.collectAsState()
    val savedFusionId by viewModel.savedFusionId.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val actionResult by viewModel.actionResult.collectAsState()

    var caption by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

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

    // 📢 Snackbar + navegación
    LaunchedEffect(actionResult) {
        actionResult?.let {
            snackbarHostState.showSnackbar(it)

            navController.navigate("fusion_history") {
                popUpTo("fusion_history") { inclusive = true }
                launchSingleTop = true
            }

            viewModel.clearActionResult()
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

            // 🖼️ IMAGEN
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

            // ✍️ CAPTION
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Texto de publicación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!fromHistory) {

                Button(
                    onClick = {
                        viewModel.saveFusion(
                            photoId!!.toInt(),
                            distributorId!!.toInt(),
                            coordinate!!.toInt()
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
                            caption = caption
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
                            viewModel.publishFusion(it, caption)
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar")
                }
            }
        }

        // 🔒 OVERLAY BLOQUEO
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // 📢 Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

}