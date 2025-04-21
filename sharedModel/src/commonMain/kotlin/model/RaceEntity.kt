package model

import converters.BooleanAsIntSerializer
import kotlinx.serialization.Serializable

@Serializable
open class RaceEntity (
    open var raceId: Int = 0,
    var raceName : String = "",
    var groundSpeed: Int = 30,
    var abilityBonuses: List<AbilityBonus>? = null,
    open var abilityBonusChoice: AbilityBonusChoice? = null,
    var alignment: String? = null,
    var age : String = "",
    var size: String = "Medium",
    var sizeDesc: String = "",
    var startingProficiencies: List<Proficiency> = listOf(),
    var proficiencyChoices : List<ProficiencyChoice> = ArrayList(),
    var languages: List<Language> = listOf(),
    var languageChoices: List<LanguageChoice> = listOf(),
    var languageDesc: String = "",
    @Serializable(with = BooleanAsIntSerializer::class)
    var isHomebrew : Boolean = false
)
