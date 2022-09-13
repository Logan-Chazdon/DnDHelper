package gmail.loganchazdon.dndhelper.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters

@Entity(tableName="races")
@TypeConverters(Converters::class)
data class Race (
    val name : String = "",
    val groundSpeed: Int = 30,
    var abilityBonuses: List<AbilityBonus>? = null,
    val abilityBonusChoice: AbilityBonusChoice? = null,
    val alignment: String? = null,
    val age : String = "",
    val size: String = "Medium",
    val sizeDesc: String = "",
    val startingProficiencies: List<Proficiency> = listOf(),
    val proficiencyChoices : List<ProficiencyChoice> = listOf(),
    val languages: List<Language> = listOf(),
    val languageChoices: List<LanguageChoice> = listOf(),
    val languageDesc: String = "",
    val traits: List<Feature> = listOf(),
    val subraces: List<Subrace>? = listOf(),
    var subrace: Subrace? = null
) {
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

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