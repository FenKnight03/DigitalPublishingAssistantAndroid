package com.ljdit.digitalpublishing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.FusionsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FusionViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val _preview = MutableStateFlow<FusionPreviewResponse?>(null)
    val preview: StateFlow<FusionPreviewResponse?> = _preview

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _fusions = MutableStateFlow<FusionsData?>(null)
    val fusions: StateFlow<FusionsData?> = _fusions

    fun generatePreview(
        photoId: Int,
        logoId: Int,
        coordinate: Int,
        caption: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val request = FusionPreviewRequest(
                logo_id = logoId,
                coordenada = coordinate,
                caption = caption
            )

            try {
                Log.d("Fusion", "Enviando request preview photoId=$photoId")

                val response = repository.createFusionPreview(photoId, request)

                Log.d("Fusion", "Response code: ${response.code()}")
                Log.d("Fusion", "Body: ${response.body()}")

                if (response.isSuccessful) {
                    _preview.value = response.body()
                } else {
                    Log.d("Fusion", "Preview error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Fusion", "Error generando preview", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFusions() {
        viewModelScope.launch {
            try {
                val response = repository.getFusions()

                if (response.isSuccessful && response.body()?.ok == true) {
                    _fusions.value = response.body()!!.data
                } else {
                    Log.e("Fusion", "Error cargando fusiones")
                }
            } catch (e: Exception) {
                Log.e("Fusion", "Error cargando fusiones", e)
            }
        }
    }

    fun loadFusionById(fusionId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repository.getFusionFull(fusionId)

                Log.d("FusionDebug", "CODE: ${response.code()}")
                Log.d("FusionDebug", "BODY: ${response.body()}")

                if (response.isSuccessful && response.body()?.ok == true) {
                    _preview.value = response.body()
                } else {
                    Log.e("FusionDebug", "ERROR: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FusionDebug", "Error cargando fusion $fusionId", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
