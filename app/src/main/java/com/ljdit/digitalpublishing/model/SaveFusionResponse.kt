package com.ljdit.digitalpublishing.model

data class SaveFusionResponse(
    val ok: Boolean,
    val data: SaveFusionData
)

data class SaveFusionData(
    val id_fusion: Int,
    val mensaje: String
)