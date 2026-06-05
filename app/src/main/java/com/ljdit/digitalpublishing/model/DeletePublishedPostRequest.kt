package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class DeletePublishedPostRequest(
    @SerializedName("fusion_id")
    val fusionId: Int
)

data class DeletePublishedPostResponse(
    val success: Boolean = false,
    val message: String? = null
)

data class DeleteFusionRequest(
    @SerializedName("fusion_id")
    val fusionId: Int
)

data class DeleteFusionResponse(
    val success: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
