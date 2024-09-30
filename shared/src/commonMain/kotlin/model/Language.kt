package model

import kotlinx.serialization.Serializable

@Serializable
data class Language (val name: String?, var index : String? = null)
