data class PublishRequest(
    val id_fusion: Int,
    val caption: String,
    val scheduled_time: Long? = null,
    val platforms: List<String>? = null
)
