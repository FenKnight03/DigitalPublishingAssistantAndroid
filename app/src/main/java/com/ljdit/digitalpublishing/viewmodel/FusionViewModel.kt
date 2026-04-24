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

    private val _savedFusionId = MutableStateFlow<Int?>(null)
    val savedFusionId: StateFlow<Int?> = _savedFusionId

    private val _publishState = MutableStateFlow<String?>(null)
    val publishState: StateFlow<String?> = _publishState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _actionResult = MutableStateFlow<String?>(null)
    val actionResult: StateFlow<String?> = _actionResult

    fun generatePreview(
        photoId: Int,
        logoId: Int,
        coordinate: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            val request = FusionPreviewRequest(
                logo_id = logoId,
                coordenada = coordinate
            )

            Log.d("Fusion", "Enviando request...")

            val response = repository.createFusionPreview(photoId, request)

            _isLoading.value = false

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
    fun saveFusion(photoId: Int, distributorId: Int, coordinate: Int) {
        viewModelScope.launch {

            _isProcessing.value = true

            val response = repository.saveFusion(photoId, distributorId, coordinate)

            if (response.isSuccessful && response.body()?.ok == true) {
                _actionResult.value = "Guardado correctamente"
            } else {
                _actionResult.value = "Error al guardar"
            }

            _isProcessing.value = false
        }
    }

    private val _fusions = MutableStateFlow<FusionsData?>(null)
    val fusions: StateFlow<FusionsData?> = _fusions

    fun loadFusions() {
        viewModelScope.launch {
            val response = repository.getFusions()

            if (response.isSuccessful && response.body()?.ok == true) {
                _fusions.value = response.body()!!.data
            } else {
                Log.e("Fusion", "Error cargando fusiones")
            }
        }
    }

    fun loadFusionById(fusionId: Int) {
        viewModelScope.launch {

            val response = repository.getFusionFull(fusionId)

            Log.d("FusionDebug", "CODE: ${response.code()}")
            Log.d("FusionDebug", "BODY: ${response.body()}")

            if (response.isSuccessful && response.body()?.ok == true) {
                _preview.value = response.body()
            } else {
                Log.e("FusionDebug", "ERROR: ${response.errorBody()?.string()}")
            }
        }
    }

    fun publishFusion(fusionId: Int, caption: String) {
        viewModelScope.launch {

            _isProcessing.value = true

            val response = repository.publishFusion(fusionId, caption)

            if (response.isSuccessful && response.body()?.success == true) {
                _actionResult.value = "Publicado correctamente"
            } else {
                _actionResult.value = "Error al publicar"
                Log.d("PublishDebug", "CODE: ${response.code()}")
                Log.d("PublishDebug", "BODY: ${response.body()}")
                Log.d("PublishDebug", "ERROR: ${response.errorBody()?.string()}")
            }

            _isProcessing.value = false
        }
    }

    fun saveAndPublish(
        photoId: Int,
        distributorId: Int,
        coordinate: Int,
        caption: String
    ) {
        viewModelScope.launch {

            _isProcessing.value = true

            val saveResponse = repository.saveFusion(photoId, distributorId, coordinate)

            if (!saveResponse.isSuccessful || saveResponse.body()?.ok != true) {
                _actionResult.value = "Error al guardar"
                _isProcessing.value = false
                return@launch
            }

            val fusionId = saveResponse.body()?.data?.id_fusion

            val publishResponse = repository.publishFusion(fusionId!!, caption)

            if (publishResponse.isSuccessful && publishResponse.body()?.success == true) {
                _actionResult.value = "Publicado correctamente"
            } else {
                _actionResult.value = "Error al publicar"
                Log.e("Publish", "Error: ${publishResponse.errorBody()?.string()}")
            }

            _isProcessing.value = false
        }
    }
    fun clearActionResult() {
        _actionResult.value = null
    }
}