package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class FusionPreviewResponse(
    val ok: Boolean,
    val data: FusionPreviewData
)

data class FusionPreviewData(
    val image: String,

    @SerializedName("photo_id")
    val photoId: Int? = null,

    @SerializedName("distributor_id")
    val distributorId: Int? = null,

    @SerializedName("logo_id")
    val logoId: Int? = null,

    @SerializedName("logo_nombre")
    val logoNombre: String? = null,

    val x: Int? = null,
    val y: Int? = null,

    @SerializedName("coordenada")
    val coordinate: Int? = null,

    val caption: String? = null,

    val formato: String? = null,

    @SerializedName("formato_display")
    val formatoDisplay: String? = null,

    val platform: PhotoPlatform? = null,

    val platforms: List<PhotoPlatform>? = emptyList()
)

fun FusionPreviewData.previewDisplayPlatforms(): List<PhotoPlatform> =
    platforms.orEmpty().takeIf { it.isNotEmpty() }
        ?: platform?.let { listOf(it) }
        ?: emptyList()
