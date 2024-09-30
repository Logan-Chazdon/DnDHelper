package model.choiceEntities

open class RaceChoiceEntity(
    val raceId: Int = 0,
    val characterId: Int = 0,
    open var abilityBonusChoice: List<String> = emptyList(),
    var proficiencyChoice: List<List<String>> = emptyList(),
    var languageChoice: List<List<String>> = emptyList(),
    var abilityBonusOverrides: List<model.AbilityBonus>? = null
)