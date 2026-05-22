package com.ljdit.digitalpublishing.model

import com.google.gson.annotations.SerializedName

data class ConnectionStatus(
    @SerializedName("facebook_connected")
    val facebookConnected: Boolean = false,

    @SerializedName("instagram_connected")
    val instagramConnected: Boolean = false,

    @SerializedName("facebook_page_id")
    val facebookPageId: String? = null,

    @SerializedName("instagram_user_id")
    val instagramUserId: String? = null,

    @SerializedName("distribuidor_id")
    val distributorId: Int? = null,

    val message: String? = null
) {
    val metaTokenConfigured: Boolean
        get() = facebookConnected || instagramConnected

    val isConnected: Boolean
        get() = facebookConnected || instagramConnected
}
