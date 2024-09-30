package model.choiceEntities

open class BackgroundChoiceEntity(
    val characterId: Int,
    val backgroundId: Int,
    val languageChoices : List<List<String>>
)