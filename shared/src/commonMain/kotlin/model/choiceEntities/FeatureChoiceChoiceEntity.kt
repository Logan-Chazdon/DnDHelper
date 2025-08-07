package model.choiceEntities

import kotlinx.serialization.Serializable

@Serializable
open class FeatureChoiceChoiceEntity(
    val featureId: Int = 0,
    val characterId: Int = 0,
    val choiceId: Int = 0
)