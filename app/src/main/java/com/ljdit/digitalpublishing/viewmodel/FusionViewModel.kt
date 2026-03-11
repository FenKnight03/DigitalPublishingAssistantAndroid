package com.ljdit.digitalpublishing.viewmodel

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

            val response = repository.createFusionPreview(photoId, request)

            if (response.isSuccessful) {
                _preview.value = response.body()
            }

        }

    }
}