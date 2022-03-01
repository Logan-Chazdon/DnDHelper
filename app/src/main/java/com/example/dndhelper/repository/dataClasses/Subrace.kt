package com.example.dndhelper.repository.dataClasses



data class Subrace(
    val name : String,
    val abilityBonuses: List<AbilityBonus>?,
    val abilityBonusChoice: AbilityBonusChoice?,
    val startingProficiencies: List<Proficiency>?,
    val languages : List<Language>,
    val languageChoices: List<LanguageChoice>,
    val traits: List<Feature>,
    val size: String?,
    val groundSpeed: Int?
)