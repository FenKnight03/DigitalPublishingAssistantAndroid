package com.ljdit.digitalpublishing.model

data class PhotoFilters(

    val formatos: Set<String> = emptySet(),

    val origenes: Set<String> = emptySet(),

    val contenidos: Set<String> = emptySet(),

    val orden: String = "recientes"
)