package com.ljdit.digitalpublishing.data.repository

import com.ljdit.digitalpublishing.data.api.RetrofitClient
import com.ljdit.digitalpublishing.model.FusionPreviewRequest

class PhotoRepository {

    suspend fun getPhotos() =
        RetrofitClient.api.getPhotos()

    suspend fun createFusionPreview(
        photoId: Int,
        request: FusionPreviewRequest
    ) =
        RetrofitClient.api.createFusionPreview(photoId, request)

}