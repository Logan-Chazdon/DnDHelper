package gmail.loganchazdon.dndhelper.model

import androidx.room.Embedded


class Race(
    id: Int = 0,
    name : String = "",
    groundSpeed: Int = 30,
    abilityBonuses: List<AbilityBonus>? = null,
    abilityBonusChoice: AbilityBonusChoice? = null,
    alignment: String? = null,
    age : String = "",
    size: String = "Medium",
    sizeDesc: String = "",
    var traits: List<Feature>? = listOf(),
    startingProficiencies: List<Proficiency> = listOf(),
    proficiencyChoices : List<ProficiencyChoice> = listOf(),
    languages: List<Language> = listOf(),
    languageChoices: List<LanguageChoice> = listOf(),
    languageDesc: String = "",
    @Embedded(prefix = "subrace")
    var subrace: Subrace? = null
) : RaceEntity(
    raceId = id,
    raceName = name,
    groundSpeed = groundSpeed,
    abilityBonuses = abilityBonuses,
    abilityBonusChoice = abilityBonusChoice,
    alignment = alignment,
    age = age,
    size = size,
    sizeDesc = sizeDesc,
    startingProficiencies = startingProficiencies,
    proficiencyChoices = proficiencyChoices,
    languages = languages,
    languageChoices = languageChoices,
    languageDesc = languageDesc,
) {
    val totalGroundSpeed: Int
    get() {
        return maxOf(groundSpeed, subrace?.groundSpeed ?: 0)
    }

    val allFeats: List<Feat>
    get() {
        val result = mutableListOf<Feat>()
        subrace?.let {
            it.featChoices?.forEach {
                it.chosen?.let { feats -> result.addAll(feats) }
            }
        }
        return result
    }

    private val allRaceLanguages: List<Language>
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

    fun longRest() {
        traits!!.forEach {
            it.recharge(1)
        }
    }

    fun getAllTraits() : List<Feature> {
        return mutableListOf<Feature>().run {
            subrace?.traits?.let {this.addAll(it)}
            traits.let {this.addAll(filterRaceFeatures())}
            this
        }
    }

    fun filterRaceFeatures(): List<Feature> {
        return traits!!.filter { feature ->
            subrace?.traits?.none { it.index == feature.index }
                ?: true
        }
    }

    fun getAllLanguages(): List<Language> {
        return if(!(subrace?.languages.isNullOrEmpty() &&
                    subrace?.languageChoices.isNullOrEmpty())) {
            subrace!!.allSubracesLanguages
        } else {
            allRaceLanguages
        }
    }

    fun getAllAbilityScoreBonuses() : List<AbilityBonus> {
        val result = mutableListOf<AbilityBonus>()
        abilityBonuses?.let { result.addAll(it) }
        subrace?.totalAbilityBonuses?.let { result.addAll(it) }
        abilityBonusChoice?.chosen?.let {result.addAll(it)}
        return result
    }

    fun getAllProficiencies(): List<Proficiency> {
        val result = mutableListOf<Proficiency>()
        result.addAll(startingProficiencies)
        proficiencyChoices.forEach {
            it.chosen.let { chosen -> result.addAll(chosen) }
        }
        return result
    }
}