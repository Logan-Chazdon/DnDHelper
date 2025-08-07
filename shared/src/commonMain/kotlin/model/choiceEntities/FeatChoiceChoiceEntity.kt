package model.choiceEntities

import kotlinx.serialization.Serializable

@Serializable
open class FeatChoiceChoiceEntity(
    val characterId: Int,
    val choiceId: Int,
    val featId: Int,
)