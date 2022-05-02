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
    val groundSpeed: Int?,
    val featChoices: List<FeatChoice>? = null
) {
    val totalAbilityBonuses: List<AbilityBonus>
    get() {
        val result = mutableListOf<AbilityBonus>()
        abilityBonuses?.let { result.addAll(it) }
        abilityBonusChoice?.chosen?.let {result.addAll(it)}
        return result
    }

    val allSubracesLanguages: List<Language>
    get() {
        val result = mutableListOf<Language>()
        result.addAll(languages)
        result.addAll(languageChoices.let { languageChoices ->
            val langs = mutableListOf<Language>()
            languageChoices.forEach {
                it.chosen?.let { chosen -> langs.addAll(chosen) }
            }
            langs
        })
        return result
    }

    val featsGranted : List<Feat>
    get() {
        val result = mutableListOf<Feat>()
        featChoices?.forEach {
            it.chosen?.let { feats -> result.addAll(feats) }
        }
        return result
    }
}