package com.ljdit.digitalpublishing.data.repository

import com.ljdit.digitalpublishing.data.api.RetrofitClient
import com.ljdit.digitalpublishing.model.FusionPreviewRequest

class PhotoRepository {

    suspend fun getPhotos() =
        RetrofitClient.photoApi.getPhotos()

    suspend fun createFusionPreview(
        photoId: Int,
        request: FusionPreviewRequest
    ) =
        RetrofitClient.photoApi.createFusionPreview(photoId, request)

}