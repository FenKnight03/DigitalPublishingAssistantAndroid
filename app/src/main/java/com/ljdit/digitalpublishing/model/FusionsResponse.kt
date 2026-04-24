package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

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

    @SerializedName("thumbnail_url")
    val thumbnail_url: String,

    @SerializedName("distributor_name")
    val distributor_name: String,

    @SerializedName("producto_nombre")
    val producto_nombre: String?,

    val formato: String?,

    val fecha_publicacion: String?

)