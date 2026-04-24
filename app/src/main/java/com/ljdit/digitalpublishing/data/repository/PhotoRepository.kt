package com.ljdit.digitalpublishing.data.repository

import PublishRequest
import PublishResponse
import com.ljdit.digitalpublishing.data.api.RetrofitClient
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.FusionsResponse
import com.ljdit.digitalpublishing.model.SaveFusionResponse
import retrofit2.Response

class PhotoRepository {

    suspend fun getPhotos() =
        RetrofitClient.photoApi.getPhotos()

    suspend fun createFusionPreview(
        photoId: Int,
        request: FusionPreviewRequest
    ) =
        RetrofitClient.photoApi.createFusionPreview(photoId, request)

    suspend fun saveFusion(
        photoId: Int,
        distributorId: Int,
        coordinate: Int
    ): Response<SaveFusionResponse> {

        return RetrofitClient.photoApi.saveFusion(
            photoId,
            FusionPreviewRequest(
                logo_id = distributorId,
                coordenada = coordinate
            )
        )
    }

    suspend fun getFusions(): Response<FusionsResponse> {
        return RetrofitClient.photoApi.getFusions()
    }

    suspend fun getFusionFull(fusionId: Int) =
        RetrofitClient.photoApi.getFusionFull(fusionId)

    suspend fun publishFusion(
        fusionId: Int,
        caption: String,
        scheduledTime: Long? = null
    ): Response<PublishResponse> {

        return RetrofitClient.photoApi.publishFusion(
            PublishRequest(
                id_fusion = fusionId,
                caption = caption,
                scheduled_time = scheduledTime
            )
        )
    }

}