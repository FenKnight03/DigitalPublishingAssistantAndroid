package com.ljdit.digitalpublishing.viewmodel

import PublishResponse
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
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FusionViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val publishDateFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss Z",
        Locale.US
    ).apply {
        timeZone = TimeZone.getDefault()
    }

    private val _preview = MutableStateFlow<FusionPreviewResponse?>(null)
    val preview: StateFlow<FusionPreviewResponse?> = _preview

    private val _savedFusionId = MutableStateFlow<Int?>(null)
    val savedFusionId: StateFlow<Int?> = _savedFusionId

    private val _publishState = MutableStateFlow<String?>(null)
    val publishState: StateFlow<String?> = _publishState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isProcessing = MutableStateFlow(false)
    private val _processingMessage =
        MutableStateFlow<String?>(null)

    val processingMessage: StateFlow<String?>
        = _processingMessage

    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _actionResult = MutableStateFlow<String?>(null)
    val actionResult: StateFlow<String?> = _actionResult

    private fun scheduledTimeLabel(scheduledTime: Long?): String =
        scheduledTime?.let {
            val millis = if (it > 9_999_999_999L) it else it * 1000
            "$it (${publishDateFormat.format(Date(millis))})"
        } ?: "null"

    private fun logPublishRequest(
        source: String,
        fusionId: Int,
        caption: String,
        scheduledTime: Long?
    ) {
        Log.d(
            "Publish",
            "$source request id_fusion=$fusionId, captionLength=${caption.length}, " +
                "scheduledTime=${scheduledTimeLabel(scheduledTime)}"
        )
    }

    private fun logPublishResponse(
        source: String,
        response: Response<PublishResponse>,
        errorText: String?
    ) {
        Log.d("Publish", "$source url=${response.raw().request.url}")
        Log.d("Publish", "$source code=${response.code()} message=${response.message()}")
        Log.d("Publish", "$source body=${response.body()}")
        Log.d("Publish", "$source errorBody=$errorText")
    }

    private fun readPublishErrorBody(response: Response<PublishResponse>): String? =
        try {
            response.errorBody()?.string()
        } catch (e: Exception) {
            "No se pudo leer error body: ${e.message}"
        }

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

            Log.d("Fusion", "Enviando request preview photoId=$photoId")

            val response = repository.createFusionPreview(photoId, request)

            _isLoading.value = false

            Log.d("Fusion", "Response code: ${response.code()}")
            Log.d("Fusion", "Body: ${response.body()}")

            if (response.isSuccessful) {
                _preview.value = response.body()
            } else {
                Log.d("Publish", "Preview error: ${response.errorBody()?.string()}")
            }
        }
    }

    fun saveFusion(photoId: Int, distributorId: Int, coordinate: Int, caption: String) {
        viewModelScope.launch {
            startProcessing("Guardando...")

            try {
                val response = repository.saveFusion(
                    photoId = photoId,
                    distributorId = distributorId,
                    coordinate = coordinate,
                    caption = caption
                )

                if (response.isSuccessful && response.body()?.ok == true) {
                    _savedFusionId.value = response.body()?.data?.id_fusion
                    _actionResult.value = "Guardado correctamente"
                } else {
                    _actionResult.value = "Error al guardar"
                }
            } catch (e: Exception) {
                Log.e("Fusion", "Error guardando fusion", e)
                _actionResult.value = "Error al guardar"
            } finally {
                stopProcessing()
            }
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

    fun publishFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long? = null
    ) {
        viewModelScope.launch {
            startProcessing(

                if (scheduledTime != null) {
                    "Agendando publicación..."
                } else {
                    "Publicando..."
                }
            )

            try {
                logPublishRequest(
                    source = "publishFusion",
                    fusionId = fusionId,
                    caption = caption,
                    scheduledTime = scheduledTime
                )

                val response = repository.publishFusion(fusionId, caption, scheduledTime)

                if (response.isSuccessful && response.body()?.success == true) {
                    _actionResult.value =
                        if (scheduledTime != null) {
                            "Publicacion agendada correctamente"
                        } else {
                            "Publicado correctamente"
                        }
                } else {
                    val errorText = readPublishErrorBody(response)
                    logPublishResponse("publishFusion", response, errorText)
                    _actionResult.value = errorText ?: "Error al publicar"
                }
            } catch (e: Exception) {
                Log.e("PublishCrash", "Exception publicando fusion $fusionId", e)

                _actionResult.value =
                    if (e is java.net.SocketTimeoutException) {
                        "Publicado correctamente (puede tardar en reflejarse)"
                    } else {
                        "Error de red"
                    }
            } finally {
                stopProcessing()
            }
        }
    }

    fun saveAndPublish(
        photoId: Int,
        distributorId: Int,
        coordinate: Int,
        caption: String,
        scheduledTime: Long? = null
    ) {
        viewModelScope.launch {
            startProcessing(

                if (scheduledTime != null) {
                    "Agendando publicación..."
                } else {
                    "Publicando..."
                }
            )

            try {
                val saveResponse = repository.saveFusion(
                    photoId = photoId,
                    distributorId = distributorId,
                    coordinate = coordinate,
                    caption = caption
                )

                if (!saveResponse.isSuccessful || saveResponse.body()?.ok != true) {
                    _actionResult.value = "Error al guardar"
                    return@launch
                }

                val fusionId = saveResponse.body()?.data?.id_fusion

                _processingMessage.value =

                    if (scheduledTime != null) {
                        "Agendando publicación..."
                    } else {
                        "Publicando..."
                    }

                if (fusionId == null) {
                    _actionResult.value = "Error: no se obtuvo id de fusion"
                    return@launch
                }

                logPublishRequest(
                    source = "saveAndPublish",
                    fusionId = fusionId,
                    caption = caption,
                    scheduledTime = scheduledTime
                )

                val publishResponse = repository.publishFusion(fusionId, caption, scheduledTime)

                if (publishResponse.isSuccessful && publishResponse.body()?.success == true) {
                    _actionResult.value =
                        if (scheduledTime != null) {
                            "Publicacion agendada correctamente"
                        } else {
                            "Publicado correctamente"
                        }
                } else {
                    val errorText = readPublishErrorBody(publishResponse)
                    logPublishResponse("saveAndPublish", publishResponse, errorText)
                    _actionResult.value = errorText ?: "Error al publicar"
                }
            } catch (e: Exception) {
                Log.e("Publish", "Error publicando", e)

                _actionResult.value =
                    if (e is java.net.SocketTimeoutException) {
                        "Instagram tardo demasiado en responder.\n\n" +
                            "La publicacion podria haberse realizado correctamente.\n" +
                            "Verifica tu perfil."
                    } else {
                        "Ocurrio un error inesperado al publicar."
                    }
            } finally {
                stopProcessing()
            }
        }
    }

    fun clearActionResult() {
        _actionResult.value = null
    }

    private fun startProcessing(message: String) {

        _processingMessage.value = message

        _isProcessing.value = true
    }

    private fun stopProcessing() {

        _isProcessing.value = false

        _processingMessage.value = null
    }
}
