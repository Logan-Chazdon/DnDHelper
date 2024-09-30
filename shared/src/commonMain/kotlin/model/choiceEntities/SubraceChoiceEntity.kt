package model.choiceEntities

open class SubraceChoiceEntity(
    val subraceId : Int,
    val characterId: Int,
    var languageChoice: List<List<String>> = emptyList(),
    open var abilityBonusChoice: List<String> = emptyList(),
    var abilityBonusOverrides: List<model.AbilityBonus>? = null
)