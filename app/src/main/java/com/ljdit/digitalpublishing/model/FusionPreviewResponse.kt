package com.ljdit.digitalpublishing.model

data class FusionPreviewResponse(
    val ok: Boolean,
    val data: PreviewData
)

data class PreviewData(
    val image: String
)