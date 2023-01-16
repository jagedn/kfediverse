package soy.aguilera.fediverse

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(val id: String, val error:String?=null)