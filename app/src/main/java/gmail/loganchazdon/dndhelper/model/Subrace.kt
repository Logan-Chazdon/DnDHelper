package gmail.loganchazdon.dndhelper.model



data class Subrace(
    var name : String,
    var abilityBonuses: List<AbilityBonus>?,
    var abilityBonusChoice: AbilityBonusChoice?,
    var startingProficiencies: List<Proficiency>?,
    var languages : List<Language>,
    var languageChoices: List<LanguageChoice>,
    var traits: List<Feature>,
    var size: String?,
    var groundSpeed: Int?,
    var featChoices: List<FeatChoice>? = null
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