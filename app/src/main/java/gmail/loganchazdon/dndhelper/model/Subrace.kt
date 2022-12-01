package gmail.loganchazdon.dndhelper.model



class Subrace(
    name : String,
    abilityBonuses: List<AbilityBonus>?,
    abilityBonusChoice: AbilityBonusChoice?,
    startingProficiencies: List<Proficiency>?,
    languages : List<Language>,
    languageChoices: List<LanguageChoice>,
    var traits: List<Feature>? = emptyList(),
    size: String?,
    groundSpeed: Int?,
    var featChoices: List<FeatChoice>? = null
) : SubraceEntity(name, abilityBonuses, abilityBonusChoice, startingProficiencies, languages, languageChoices, size, groundSpeed){
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