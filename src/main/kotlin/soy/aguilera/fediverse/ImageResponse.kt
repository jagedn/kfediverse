package soy.aguilera.fediverse

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse( val id: String, val url: String)