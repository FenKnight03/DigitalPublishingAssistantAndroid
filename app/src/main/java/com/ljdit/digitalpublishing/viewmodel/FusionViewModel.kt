package com.ljdit.digitalpublishing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FusionViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val _preview = MutableStateFlow<FusionPreviewResponse?>(null)
    val preview: StateFlow<FusionPreviewResponse?> = _preview

    fun generatePreview(
        photoId: Int,
        logoId: Int,
        coordinate: Int
    ) {

        viewModelScope.launch {

            val request = FusionPreviewRequest(
                logo_id = logoId,
                coordenada = coordinate
            )

            Log.d("Fusion", "Enviando request...")

            val response = repository.createFusionPreview(photoId, request)

            Log.d("Fusion", "Response code: ${response.code()}")
            Log.d("Fusion", "Body: ${response.body()}")

            println("FUSION CODE: ${response.code()}")

            if (response.isSuccessful) {

                println("FUSION OK")

                _preview.value = response.body()

            } else {

                println("FUSION ERROR BODY: ${response.errorBody()?.string()}")

            }

        }

    }
}