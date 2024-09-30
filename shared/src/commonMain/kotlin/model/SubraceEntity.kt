package model


open class SubraceEntity(
    var name : String,
    var abilityBonuses: List<AbilityBonus>? = null,
    var abilityBonusChoice: AbilityBonusChoice?= null,
    var startingProficiencies: List<Proficiency>?= null,
    var languages : List<Language> = emptyList(),
    var languageChoices: List<LanguageChoice> = emptyList(),
    var size: String? = "Medium",
    var groundSpeed: Int? = null,
    var isHomebrew : Boolean = false,
)  {
    open var id: Int = 0
}
