package com.ljdit.digitalpublishing.model

data class FusionsResponse(
    val ok: Boolean,
    val data: FusionsData
)

data class FusionsData(
    val pendientes: List<FusionItem>,
    val agendadas: List<FusionItem>,
    val publicadas: List<FusionItem>
)

data class FusionItem(
    val id: Int,
    val photo_id: Int,
    val distributor_name: String,
    val coordenada: Int,
    val fecha_publicacion: String?,
    val thumbnailUrl: String
)