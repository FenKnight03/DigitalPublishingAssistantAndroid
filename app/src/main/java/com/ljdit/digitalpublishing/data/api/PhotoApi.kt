package com.ljdit.digitalpublishing.data.api

import PublishRequest
import PublishResponse
import com.ljdit.digitalpublishing.model.Distributor
import com.ljdit.digitalpublishing.model.FusionPreviewRequest
import com.ljdit.digitalpublishing.model.FusionPreviewResponse
import com.ljdit.digitalpublishing.model.FusionsResponse
import com.ljdit.digitalpublishing.model.Photo
import com.ljdit.digitalpublishing.model.SaveFusionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface PhotoApi {

    @GET("api/media_library/photos/")
    suspend fun getPhotos(): Response<List<Photo>>

    @GET("api/media_library/distributors/")
    suspend fun getDistributors(): Response<List<Distributor>>

    @POST("api/media_library/fusion/preview/{photoId}/")
    suspend fun createFusionPreview(
        @Path("photoId") photoId: Int,
        @Body request: FusionPreviewRequest
    ): Response<FusionPreviewResponse>

    @POST("api/media_library/fusion/save/{photoId}/")
    suspend fun saveFusion(
        @Path("photoId") photoId: Int,
        @Body request: FusionPreviewRequest
    ): Response<SaveFusionResponse>

    @GET("api/media_library/fusions/")
    suspend fun getFusions(): Response<FusionsResponse>

    @GET("api/media_library/fusion/{id}/")
    suspend fun getFusionFull(
        @Path("id") fusionId: Int
    ): Response<FusionPreviewResponse>

    @POST("api/publishing/publicar-fusion/")
    suspend fun publishFusion(
        @Body body: PublishRequest
    ): Response<PublishResponse>

}