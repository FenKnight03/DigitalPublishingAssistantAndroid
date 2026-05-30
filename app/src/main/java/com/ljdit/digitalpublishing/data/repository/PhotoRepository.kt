package com.ljdit.digitalpublishing.data.repository

import PublishRequest
import PublishResponse
import android.util.Log
import com.ljdit.digitalpublishing.data.api.RetrofitClient
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.FusionsResponse
import com.ljdit.digitalpublishing.model.SaveFusionResponse
import retrofit2.Response

class PhotoRepository {

    private fun Long.toUnixSeconds(): Long =
        if (this > 9_999_999_999L) this / 1000 else this

    suspend fun getPhotos(

        formato: String? = null,
        origen: String? = null,
        contenido: String? = null,
        coordenadas: String? = null,
        estado: String? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null,
        orden: String? = null,
        page: Int = 1,
        pageSize: Int = 30

    ) =
        RetrofitClient.photoApi.getPhotos(

            formato = formato,
            origen = origen,
            contenido = contenido,
            coordenadas = coordenadas,
            estado = estado,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
            orden = orden,
            page = page,
            pageSize = pageSize
        )

    suspend fun createFusionPreview(
        photoId: Int,
        request: FusionPreviewRequest
    ) =
        RetrofitClient.photoApi.createFusionPreview(photoId, request)

    suspend fun saveFusion(
        photoId: Int,
        logoId: Int,
        coordinate: Int,
        caption: String? = null
    ): Response<SaveFusionResponse> {

        return RetrofitClient.photoApi.saveFusion(
            photoId,
            FusionPreviewRequest(
                logo_id = logoId,
                coordenada = coordinate,
                caption = caption?.takeIf { it.isNotBlank() }
            )
        )
    }

    suspend fun getFusions(): Response<FusionsResponse> {
        return RetrofitClient.photoApi.getFusions()
    }

    suspend fun getFusionFull(fusionId: Int) =
        RetrofitClient.photoApi.getFusionFull(fusionId)

    suspend fun getDistributors() =
        RetrofitClient.photoApi.getDistributors()

    suspend fun getConnectionStatus() =
        RetrofitClient.photoApi.getConnectionStatus()

    suspend fun publishFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long? = null,
        platforms: List<String>? = null
    ): Response<PublishResponse> {

        val scheduledTimeSeconds = scheduledTime?.toUnixSeconds()
        val platformKeys = platforms
            ?.map { it.trim().lowercase() }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?.takeIf { it.isNotEmpty() }

        Log.d(
            "PublishRequest",
            "id_fusion=$fusionId, captionLength=${caption.length}, " +
                "scheduledTimeInput=$scheduledTime, scheduled_time=$scheduledTimeSeconds, " +
                "platforms=$platformKeys"
        )

        return RetrofitClient.photoApi.publishFusion(
            PublishRequest(
                id_fusion = fusionId,
                caption = caption,
                scheduled_time = scheduledTimeSeconds,
                platforms = platformKeys
            )
        )
    }

}
