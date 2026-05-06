package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class FusionPreviewResponse(
    val ok: Boolean,
    val data: FusionPreviewData
)

data class FusionPreviewData(
    val image: String,

    @SerializedName("logo_id")
    val logoId: Int,

    @SerializedName("logo_nombre")
    val logoNombre: String,

    val x: Int,
    val y: Int,

    @SerializedName("coordenada")
    val coordinate: Int
)