package com.ljdit.digitalpublishing.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ljdit.digitalpublishing.viewmodel.FusionViewModel

@Composable
fun FusionPreviewScreen(
    photoId: String?,
    distributorId: String?,
    coordinate: String?,
    viewModel: FusionViewModel = viewModel()
) {

    val preview by viewModel.preview.collectAsState()

    LaunchedEffect(Unit) {

        viewModel.generatePreview(
            photoId!!.toInt(),
            distributorId!!.toInt(),
            coordinate!!.toInt()
        )

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Preview de fusión")

        Spacer(modifier = Modifier.height(20.dp))

        preview?.let {

            Log.d("FusionUI", "Preview recibido: $it")

            val base64 = it.data.image
            Log.d("FusionUI", "Base64 length: ${base64.length}")

            val imageBytes = Base64.decode(base64, Base64.DEFAULT)

            val bitmap = BitmapFactory.decodeByteArray(
                imageBytes,
                0,
                imageBytes.size
            )

            if (bitmap == null) {
                Text("Error al decodificar imagen")
            } else {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Fusion preview"
                )
            }

        } ?: Text("Cargando, Sin preview aún, si se tarda mucho probablemente error")

    }
}