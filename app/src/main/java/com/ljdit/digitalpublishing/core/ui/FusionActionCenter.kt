package com.ljdit.digitalpublishing.core.ui

import android.util.Log
import com.ljdit.digitalpublishing.core.session.SessionManager
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
    val title: String? = null,
    val message: String? = null,
    val resultTitle: String? = null,
    val resultMessage: String? = null,
    val requiresLogout: Boolean = false
)

object FusionActionCenter {

    private val repository = PhotoRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(FusionActionState())
    val state: StateFlow<FusionActionState> = _state

    fun dismissResult() {
        val shouldLogout = _state.value.requiresLogout
        _state.value = FusionActionState()

        if (shouldLogout) {
            SessionManager.clearPersistedSession()
        }
    }

    fun saveFusion(
        photoId: Int,
        logoId: Int,
        coordinate: Int,
        caption: String
    ) {
        runAction(
            title = "Guardando fusion",
            message = "Puedes seguir navegando. Te avisamos cuando termine."
        ) {
            val response = repository.saveFusion(
                photoId = photoId,
                logoId = logoId,
                coordinate = coordinate,
                caption = caption
            )

            when {
                response.isSuccessful && response.body()?.ok == true ->
                    ActionResult(
                        title = "Fusion guardada",
                        message = "La fusion quedo guardada y puedes verla en el historial."
                    )

                response.code() == 401 ->
                    expiredSessionResult()

                else ->
                    ActionResult(
                        title = "No se pudo guardar",
                        message = "Error al guardar la fusion."
                    )
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
            title = if (scheduledTime == null) "Publicando contenido" else "Programando publicacion",
            message = "Puedes seguir navegando. Te avisamos cuando Meta responda."
        ) {
            val saveResponse = repository.saveFusion(
                photoId = photoId,
                logoId = logoId,
                coordinate = coordinate,
                caption = caption
            )

            if (saveResponse.code() == 401) {
                return@runAction expiredSessionResult()
            }

            if (!saveResponse.isSuccessful || saveResponse.body()?.ok != true) {
                return@runAction ActionResult(
                    title = "No se pudo guardar",
                    message = "Error al guardar la fusion."
                )
            }

            val fusionId = saveResponse.body()?.data?.id_fusion
                ?: return@runAction ActionResult(
                    title = "No se pudo publicar",
                    message = "No se obtuvo el id de fusion."
                )

            publishSavedFusion(fusionId, caption, scheduledTime)
        }
    }

    fun publishFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long?
    ) {
        runAction(
            title = if (scheduledTime == null) "Publicando contenido" else "Programando publicacion",
            message = "Puedes seguir navegando. Te avisamos cuando Meta responda."
        ) {
            publishSavedFusion(fusionId, caption, scheduledTime)
        }
    }

    private suspend fun publishSavedFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long?
    ): ActionResult {
        return try {
            val response = repository.publishFusion(fusionId, caption, scheduledTime)

            when {
                response.isSuccessful && response.body()?.success == true ->
                    if (scheduledTime == null) {
                        ActionResult(
                            title = "Publicacion enviada",
                            message = "La publicacion se envio correctamente."
                        )
                    } else {
                        ActionResult(
                            title = "Publicacion programada",
                            message = "La publicacion quedo programada correctamente."
                        )
                    }

                response.code() == 401 ->
                    expiredSessionResult()

                else -> {
                    val errorText = readErrorBody(response.errorBody()?.string())
                    ActionResult(
                        title = "No se pudo publicar",
                        message = errorText ?: "Error al publicar."
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FusionActionCenter", "Error publicando fusion $fusionId", e)

            if (e is SocketTimeoutException) {
                ActionResult(
                    title = "Publicacion enviada",
                    message = "Puede tardar en reflejarse. Verifica el perfil antes de reintentar."
                )
            } else {
                ActionResult(
                    title = "No se pudo publicar",
                    message = "Error de red. Intenta nuevamente."
                )
            }
        }
    }

    private fun runAction(
        title: String,
        message: String,
        action: suspend () -> ActionResult
    ) {
        if (_state.value.isProcessing) {
            return
        }

        _state.value = FusionActionState(
            isProcessing = true,
            title = title,
            message = message
        )

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    action()
                } catch (e: Exception) {
                    Log.e("FusionActionCenter", "Error ejecutando accion", e)
                    ActionResult(
                        title = "No se pudo completar",
                        message = "Ocurrio un error inesperado."
                    )
                }
            }

            _state.value = FusionActionState(
                resultTitle = result.title,
                resultMessage = result.message,
                requiresLogout = result.requiresLogout
            )
        }
    }

    private fun expiredSessionResult(): ActionResult {
        return ActionResult(
            title = "Sesion expirada",
            message = "Tu sesion expiro. Inicia sesion nuevamente antes de publicar.",
            requiresLogout = true
        )
    }

    private fun readErrorBody(value: String?): String? =
        value?.takeIf { it.isNotBlank() }

    private data class ActionResult(
        val title: String,
        val message: String,
        val requiresLogout: Boolean = false
    )
}
