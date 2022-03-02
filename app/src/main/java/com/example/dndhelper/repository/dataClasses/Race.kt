package com.example.dndhelper.repository.dataClasses

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="races")
@TypeConverters(Converters::class)
data class Race (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name="name")
    val name : String,
    val groundSpeed: Int,
    val abilityBonuses: List<AbilityBonus>?,
    val abilityBonusChoice: AbilityBonusChoice?,
    val alignment: String?,
    val age : String,
    val size: String,
    val sizeDesc: String,
    val startingProficiencies: List<Proficiency>,
    val proficiencyChoices : List<ProficiencyChoice>,
    val languages: List<Language>,
    val languageChoices: List<LanguageChoice>,
    val languageDesc: String,
    val traits: List<Feature>,
    val subraces: List<Subrace>?,
    var subrace: Subrace? = null
) {
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