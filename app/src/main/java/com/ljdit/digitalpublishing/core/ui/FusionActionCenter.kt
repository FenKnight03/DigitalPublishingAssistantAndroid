package com.ljdit.digitalpublishing.core.ui

import android.util.Log
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

data class FusionActionState(
    val isProcessing: Boolean = false,
    val message: String? = null,
    val result: String? = null
)

object FusionActionCenter {

    private val repository = PhotoRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(FusionActionState())
    val state: StateFlow<FusionActionState> = _state

    fun clearResult() {
        _state.value = _state.value.copy(result = null)
    }

    fun saveFusion(
        photoId: Int,
        logoId: Int,
        coordinate: Int,
        caption: String
    ) {
        runAction("Guardando fusion...") {
            val response = repository.saveFusion(
                photoId = photoId,
                logoId = logoId,
                coordinate = coordinate,
                caption = caption
            )

            if (response.isSuccessful && response.body()?.ok == true) {
                "Guardado correctamente"
            } else {
                "Error al guardar"
            }
        }
    }

    fun saveAndPublish(
        photoId: Int,
        logoId: Int,
        coordinate: Int,
        caption: String,
        scheduledTime: Long?
    ) {
        runAction(
            if (scheduledTime != null) {
                "Agendando publicacion..."
            } else {
                "Publicando..."
            }
        ) {
            val saveResponse = repository.saveFusion(
                photoId = photoId,
                logoId = logoId,
                coordinate = coordinate,
                caption = caption
            )

            if (!saveResponse.isSuccessful || saveResponse.body()?.ok != true) {
                return@runAction "Error al guardar"
            }

            val fusionId = saveResponse.body()?.data?.id_fusion
                ?: return@runAction "Error: no se obtuvo id de fusion"

            publishSavedFusion(fusionId, caption, scheduledTime)
        }
    }

    fun publishFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long?
    ) {
        runAction(
            if (scheduledTime != null) {
                "Agendando publicacion..."
            } else {
                "Publicando..."
            }
        ) {
            publishSavedFusion(fusionId, caption, scheduledTime)
        }
    }

    private suspend fun publishSavedFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long?
    ): String {
        return try {
            val response = repository.publishFusion(fusionId, caption, scheduledTime)

            if (response.isSuccessful && response.body()?.success == true) {
                if (scheduledTime != null) {
                    "Publicacion agendada correctamente"
                } else {
                    "Publicado correctamente"
                }
            } else {
                val errorText = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    "No se pudo leer error: ${e.message}"
                }

                if (response.code() == 401) {
                    "Tu sesion expiro. Inicia sesion nuevamente."
                } else {
                    errorText ?: "Error al publicar"
                }
            }
        } catch (e: Exception) {
            Log.e("FusionActionCenter", "Error publicando fusion $fusionId", e)

            if (e is SocketTimeoutException) {
                "Publicado correctamente (puede tardar en reflejarse)"
            } else {
                "Error de red"
            }
        }
    }

    private fun runAction(
        message: String,
        action: suspend () -> String
    ) {
        _state.value = FusionActionState(
            isProcessing = true,
            message = message,
            result = null
        )

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    action()
                } catch (e: Exception) {
                    Log.e("FusionActionCenter", "Error ejecutando accion", e)
                    "Ocurrio un error inesperado."
                }
            }

            _state.value = FusionActionState(
                isProcessing = false,
                message = null,
                result = result
            )
        }
    }
}
