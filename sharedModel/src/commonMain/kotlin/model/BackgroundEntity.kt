package model

import converters.BooleanAsIntSerializer
import kotlinx.serialization.Serializable

@Serializable
open class BackgroundEntity(
    var name: String = "",
    var desc: String = "",
    var spells: List<Spell>? = null,
    var proficiencies : List<Proficiency> = emptyList(),
    var proficiencyChoices : List<ProficiencyChoice>? = null,
    var languages : List<Language> = emptyList(),
    var languageChoices : List<LanguageChoice>? = null,
    var equipment : List<ItemInterface> = emptyList(),
    var equipmentChoices: List<ItemChoice> = emptyList(),
    @Serializable(with = BooleanAsIntSerializer::class)
    var isHomebrew : Boolean = false
) {
    open var id : Int = 0
}