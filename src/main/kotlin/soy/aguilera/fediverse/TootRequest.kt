package soy.aguilera.fediverse

import kotlinx.serialization.Serializable

@Serializable
data class TootRequest(
    val status:String,
    val media_ids: Set<String>,
    val in_reply_to_id: String)
