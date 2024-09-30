package model

import kotlinx.serialization.Serializable

@Serializable
data class Proficiency (val name : String? = null, val index: String? = null)
