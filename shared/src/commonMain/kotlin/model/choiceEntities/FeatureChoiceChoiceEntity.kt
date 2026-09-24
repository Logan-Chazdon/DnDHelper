package model.choiceEntities

import kotlinx.serialization.Serializable

/**Used to connect a featureChoice to its list of chosen features in a character
 * @param index This is used to differentiate connections in the case that a featureChoice is connected to several copies of the same feature.
 * In most cases it will not be used.*/
@Serializable
open class FeatureChoiceChoiceEntity(
    val featureId: Int = 0,
    val characterId: Int = 0,
    val choiceId: Int = 0,
    open val index: Int = 0,
)