package model.pojos

import kotlinx.serialization.Serializable

@Serializable
data class NameAndIdPojo(
    val id: Int,
    val name: String
)
