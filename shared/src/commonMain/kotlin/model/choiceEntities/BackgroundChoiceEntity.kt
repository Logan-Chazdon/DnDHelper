package model.choiceEntities

import kotlinx.serialization.Serializable

@Serializable
open class BackgroundChoiceEntity(
    val characterId: Int,
    val backgroundId: Int,
    val languageChoices : List<List<String>>
)