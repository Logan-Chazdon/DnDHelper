package model


open class BackgroundEntity(
    val name: String,
    val desc: String,
    var spells: List<Spell>?,
    val proficiencies : List<Proficiency>,
    val proficiencyChoices : List<ProficiencyChoice>? = null,
    val languages : List<Language>,
    val languageChoices : List<LanguageChoice>? = null,
    val equipment : List<ItemInterface>,
    val equipmentChoices: List<ItemChoice>,
    var isHomebrew : Boolean = false
) {
    open var id : Int = 0
}