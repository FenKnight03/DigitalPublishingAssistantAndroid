package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class Photo(

    val id: Int,

    val imageUrl: String,

    @SerializedName("coordinates")
    val coordinates: List<PhotoCoordinate>? = null,

    val width: Float? = null,

    val height: Float? = null
)

data class PhotoCoordinate(

    val id: Int,

    val x: Float,

    val y: Float
)