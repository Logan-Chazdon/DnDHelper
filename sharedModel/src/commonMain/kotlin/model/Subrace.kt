package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Subrace() : SubraceEntity() {
    @Transient
    var traits: List<Feature>? = emptyList()
    @Transient
    var featChoices: List<FeatChoice>? = null

    constructor(
        name: String,
        abilityBonuses: List<AbilityBonus>?,
        abilityBonusChoice: AbilityBonusChoice?,
        startingProficiencies: List<Proficiency>?,
        languages: List<Language>,
        languageChoices: List<LanguageChoice>,
        traits: List<Feature>? = emptyList(),
        size: String?,
        groundSpeed: Int?,
        featChoices: List<FeatChoice>? = null
    ) : this() {
        this.name = name
        this.abilityBonuses = abilityBonuses
        this.abilityBonusChoice = abilityBonusChoice
        this.startingProficiencies = startingProficiencies
        this.languages = languages
        this.languageChoices = languageChoices
        this.size = size
        this.groundSpeed = groundSpeed
        this.traits = traits
        this.featChoices = featChoices
    }

    constructor(entity: SubraceEntity, traits: List<Feature>?, featChoices: List<FeatChoice>?) : this(
        name = entity.name,
        abilityBonuses = entity.abilityBonuses,
        abilityBonusChoice = entity.abilityBonusChoice,
        startingProficiencies = entity.startingProficiencies,
        languages = entity.languages,
        languageChoices = entity.languageChoices,
        traits = traits,
        featChoices = featChoices,
        groundSpeed = entity.groundSpeed,
        size = entity.size
    ) {
        this.id = entity.id
    }



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
                it.chosen.let { chosen -> langs.addAll(chosen) }
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