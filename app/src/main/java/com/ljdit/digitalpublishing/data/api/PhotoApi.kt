package com.ljdit.digitalpublishing.data.api

import com.ljdit.digitalpublishing.model.Distributor
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.Photo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface PhotoApi {

    @GET("home/api/photos/")
    suspend fun getPhotos(): Response<List<Photo>>

    @GET("api/distributors/")
    suspend fun getDistributors(): Response<List<Distributor>>

    @POST("home/{photoId}/preview/")
    suspend fun createFusionPreview(
        @Path("photoId") photoId: Int,
        @Body request: FusionPreviewRequest
    ): Response<FusionPreviewResponse>

}