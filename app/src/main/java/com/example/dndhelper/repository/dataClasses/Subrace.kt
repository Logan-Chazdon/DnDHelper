package com.example.dndhelper.repository.dataClasses



data class Subrace (
    val name : String,
    val desc : String,
    val abilityBonuses: List<AbilityBonus>,
    val startingProficiencies: List<Proficiency>,
    val languages : List<Language>,
    val languageOptions: List<Language>,
    val numOfLanguageChoices: Int,
    val racialTraits: List<Feature>
)