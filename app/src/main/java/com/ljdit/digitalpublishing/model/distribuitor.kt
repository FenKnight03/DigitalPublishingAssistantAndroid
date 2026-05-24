package com.ljdit.digitalpublishing.model

data class Distributor(
    val id: Int,
    val name: String,
    val logoId: Int? = null,
    val logoUrl: String,
    val logos: List<DistributorLogo> = emptyList()
)

data class DistributorLogo(
    val id: Int,
    val imageUrl: String
)
