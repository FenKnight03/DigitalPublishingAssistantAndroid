package com.ljdit.digitalpublishing.model

data class PhotosResponse(

    val count: Int,

    val next: String?,

    val previous: String?,

    val results: List<Photo>
)