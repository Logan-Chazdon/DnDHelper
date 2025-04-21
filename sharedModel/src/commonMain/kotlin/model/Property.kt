package model

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val name: String ,
    val desc: String? = null
)