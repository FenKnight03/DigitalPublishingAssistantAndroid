package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class Photo(

    val id: Int,

    val imageUrl: String,

    @SerializedName(value = "coordinates", alternate = ["coordenadas"])
    val coordinates: List<PhotoCoordinate>? = null,

    @SerializedName(value = "width", alternate = ["ancho"])
    val width: Int? = null,

    @SerializedName(value = "height", alternate = ["alto"])
    val height: Int? = null,

    val formato: String?,

    val origen: String?,

    val en_uso: Boolean?,

    val fecha_carga: String?,

    val platform: PhotoPlatform? = null,

    val producto: String? = null
)

data class PhotoCoordinate(

    val id: Int,

    @SerializedName(value = "x", alternate = ["pos_x", "posicion_x", "coordenada_x"])
    val x: Float,

    @SerializedName(value = "y", alternate = ["pos_y", "posicion_y", "coordenada_y"])
    val y: Float
)

data class PhotoPlatform(

    val key: String?,

    val name: String?,

    val iconUrl: String?
)