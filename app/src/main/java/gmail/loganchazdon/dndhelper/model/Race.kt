package gmail.loganchazdon.dndhelper.model

import androidx.room.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef


data class Race(
    override var id: Int = 0,
    override var name : String = "",
    override var groundSpeed: Int = 30,
    override var abilityBonuses: List<AbilityBonus>? = null,
    override var abilityBonusChoice: AbilityBonusChoice? = null,
    override var alignment: String? = null,
    override var age : String = "",
    override var size: String = "Medium",
    override var sizeDesc: String = "",
    override var startingProficiencies: List<Proficiency> = listOf(),
    override var proficiencyChoices : List<ProficiencyChoice> = listOf(),
    override var languages: List<Language> = listOf(),
    override var languageChoices: List<LanguageChoice> = listOf(),
    override var languageDesc: String = "",
    @Relation(
        entity = Feature::class,
        parentColumn = "id",
        entityColumn = "featureId",
        associateBy = Junction(RaceFeatureCrossRef::class)
    )
    override var traits: List<Feature> = listOf(),
    override var subraces: List<Subrace>? = listOf(),
    override var subrace: Subrace? = null
) : RaceEntity() {

    val totalGroundSpeed: Int?
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
                it.chosen?.let { chosen -> langs.addAll(chosen) }
            }
            langs
        })
        return result
    }

    fun longRest() {
        traits.forEach {
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
        return traits.filter { feature ->
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
            it.chosen?.let { chosen -> result.addAll(chosen) }
        }
        return result
    }
}