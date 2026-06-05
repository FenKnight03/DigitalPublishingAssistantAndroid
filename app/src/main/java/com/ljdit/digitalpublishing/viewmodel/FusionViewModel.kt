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

    private val _deletingPublishedPostId = MutableStateFlow<Int?>(null)
    val deletingPublishedPostId: StateFlow<Int?> = _deletingPublishedPostId

    private val _deletingFusionId = MutableStateFlow<Int?>(null)
    val deletingFusionId: StateFlow<Int?> = _deletingFusionId

    private val _historyActionMessage = MutableStateFlow<String?>(null)
    val historyActionMessage: StateFlow<String?> = _historyActionMessage

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

    fun deletePublishedPost(fusionId: Int) {
        if (_deletingPublishedPostId.value != null || _deletingFusionId.value != null) {
            return
        }

        viewModelScope.launch {
            _deletingPublishedPostId.value = fusionId
            _historyActionMessage.value = null

            try {
                val response = repository.deletePublishedPost(fusionId)

                when {
                    response.isSuccessful && response.body()?.success == true -> {
                        _historyActionMessage.value = "Publicacion eliminada de redes correctamente."
                        loadFusions()
                    }

                    response.code() == 401 ->
                        _historyActionMessage.value = "Tu sesion expiro. Inicia sesion nuevamente."

                    else -> {
                        val apiMessage = response.body()?.message
                            ?: response.errorBody()?.string()
                            ?: "No se pudo eliminar la publicacion."
                        _historyActionMessage.value = apiMessage
                    }
                }
            } catch (e: Exception) {
                Log.e("Fusion", "Error eliminando post publicado $fusionId", e)
                _historyActionMessage.value = "Error de red. Intenta nuevamente."
            } finally {
                _deletingPublishedPostId.value = null
            }
        }
    }

    fun deleteFusion(fusionId: Int) {
        if (_deletingFusionId.value != null || _deletingPublishedPostId.value != null) {
            return
        }

        viewModelScope.launch {
            _deletingFusionId.value = fusionId
            _historyActionMessage.value = null

            try {
                val response = repository.deleteFusion(fusionId)

                when {
                    response.isSuccessful && response.body()?.success == true -> {
                        _historyActionMessage.value =
                            response.body()?.message ?: "Fusion eliminada correctamente."
                        loadFusions()
                    }

                    response.code() == 401 ->
                        _historyActionMessage.value = "Tu sesion expiro. Inicia sesion nuevamente."

                    else -> {
                        val apiMessage = response.body()?.error
                            ?: response.body()?.message
                            ?: response.errorBody()?.string()
                            ?: "No se pudo eliminar la fusion."
                        _historyActionMessage.value = apiMessage
                    }
                }
            } catch (e: Exception) {
                Log.e("Fusion", "Error eliminando fusion $fusionId", e)
                _historyActionMessage.value = "Error de red. Intenta nuevamente."
            } finally {
                _deletingFusionId.value = null
            }
        }
    }

    fun dismissHistoryActionMessage() {
        _historyActionMessage.value = null
    }
}
