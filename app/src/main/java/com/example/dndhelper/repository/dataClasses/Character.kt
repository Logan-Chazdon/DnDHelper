package com.example.dndhelper.repository.dataClasses


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.model.Converters
import kotlin.math.floor

@Entity(tableName="characters")
@TypeConverters(Converters::class)
data class Character(
    @ColumnInfo(name="name")
    var name: String,
    var personalityTraits: String = "",
    var ideals: String = "",
    var bonds: String = "",
    var flaws: String = "",
    var race: Race? = null,
    var currentHp: Int = 0,
    var tempHp: Int = 0,
    var conditions: MutableList<String> = mutableListOf<String>(),
    var resistances: MutableList<String> = mutableListOf<String>(),
    var classes: MutableMap<String, Class> = mutableMapOf(),
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0,
    var statGenerationMethodIndex: Int = 0,
    var baseStats: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var abilityScoreIncreases: MutableMap<String, Int> = mutableMapOf(
        "Str" to 0,
        "Dex" to 0,
        "Con" to 0,
        "Int" to 0,
        "Wis" to 0,
        "Cha" to 0
    ),
    var background: Background? = null,
    var backpack: Backpack = Backpack(),
    var inspiration: Boolean = false,
    var equiptArmor: Armor = Armor.none,
    var feats: MutableList<Feat> = mutableListOf<Feat>(),
    var positiveDeathSaves: Int = 0,
    var negativeDeathSaves: Int = 0,
    var spellSlots: List<Resource> = listOf(),
    val addedLanguages: MutableList<Language> = mutableListOf<Language>(),
    val addedProficiencies: MutableList<Proficiency> = mutableListOf<Proficiency>()
){
    val features: List<Feature>
    get() {
        val result = mutableListOf<Feature>()
        race?.let { result.addAll(it.traits) }

        classes.values.forEach {
            it.levelPath.forEach { feature ->
                if(feature.level <= it.level) {
                    result.add(feature)
                }
            }

            it.subclass?.features?.forEach { feature ->
                if(feature.level <= it.level) {
                    result.add(feature)
                }
            }
        }

        feats.forEach {
            it.features?.let { features -> result.addAll(features) }
        }
        return result
    }

    private val realStats : MutableMap<String, Int>
    get() {
        val stats = baseStats.toMutableMap()
        //Ability Score Increases
        for(item in abilityScoreIncreases.entries) {
            stats[item.key] = stats[item.key]?.plus(item.value) ?: 0
        }

        //Feats
        for(item in feats) {
            if(item.abilityBonus != null) {
                stats[item.abilityBonus.ability] = stats[item.abilityBonus.ability]?.plus(item.abilityBonus.bonus) ?: 0
            }
            if(item.abilityBonusChoice != null) {
                item.abilityBonusChoice.chosen?.forEach {
                    stats[it.ability] = stats[it.ability]?.plus(it.bonus) ?: 0
                }
            }
        }

        //Races
        race?.abilityBonuses?.forEach {
            stats[it.ability] = stats[it.ability]?.plus(it.bonus) ?: 0
        }

        return stats
    }

    val maxHp: Int
    get() {
        var newMax = 0
        for(item in classes.values) {
            newMax += if(item.isBaseClass) {
                ((((item.hitDie / 2) + 1) + getStatMod("Con")) * (item.level -1)) + 8 + getStatMod("Con")
            } else {
                (((item.hitDie / 2) + 1) + getStatMod("Con")) * item.level
            }
        }
        return newMax
    }

    val totalClassLevels: Int
    get() {
        var result = 0
        for(item in classes.values) {
            result = item.level
        }
        return result
    }

    fun addClass(newClass: Class) {
        backpack.classCurrency = Currency.getEmptyCurrencyMap()
        backpack.classItems = mutableListOf()
        if(newClass.isBaseClass) {
            newClass.equipment.let { items ->
                for (itemInterface in items) {
                    backpack.addClassItems(listOf(itemInterface))
                }
            }

            newClass.equipmentChoices.forEach {
                it.chosen?.let { items -> backpack.classItems.addAll(items) }
            }
        }
        val alreadyHasCasterClass = classes.run {
            var result = false
            for (it in this.values) {
                if(it.spellCasting?.type ?: 0.0 != 0.0) {
                    result = true
                    break
                }
            }
            result
        }
        classes[newClass.name] = newClass


        if(alreadyHasCasterClass && newClass.spellCasting?.type ?: 0.0 != 0.0) {
            //Get spells lots from the multiclass table
            spellSlots = when(totalCasterLevels) {
                1 -> {
                    listOf(Resource(name = "1st", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"))
                }
                else -> {
                    listOf()
                }
            }
        } else {
            //Get the spell slots from the class itself
            spellSlots = newClass.spellCasting?.spellSlotsByLevel?.get(newClass.level) ?: listOf()
        }

    }



    fun setNewBackground(newBackGround: Background) {
        background = newBackGround
        backpack.backgroundCurrency = Currency.getEmptyCurrencyMap()
        backpack.backgroundItems = mutableListOf<ItemInterface>()
        background?.equipment?.let { items ->
            backpack.addBackgroundItems(items)
        }
        background?.equipmentChoices?.forEach {
            it.chosen?.let { chosen ->
                for (item in chosen) {
                    if(it.chosen != null) {
                        backpack.addBackgroundItems(it.chosen!!)
                    }
                }
            }
        }
    }


    fun getStats(): MutableMap<String, Int> {
        return realStats
    }

    fun getStat(name: String): Int? {
        return realStats[name]
    }

    fun getStatMod(name: String): Int {
        return (getStat(name)!! - 10) / 2
    }

    fun addAbilityScoreIncreases(increases: Map<String, Int>) {
        for(item in increases) {
            abilityScoreIncreases[item.key] = abilityScoreIncreases[item.key]?.plus(item.value) ?: 0
        }
    }

    fun getFormattedClasses(): String {
        var result = ""
        for((i, item) in classes.values.withIndex()) {
            result += "${item.name} ${item.level}"
            if(i != classes.size - 1) {
                result += ", "
            }
        }
        return result
    }

    fun longRest() {
        //Classes
        classes.values.forEach {
            it.longRest()
        }

        //Race
        race?.longRest()

        //Items

        //Hp
        tempHp = 0
        currentHp = maxHp
    }

    private fun checkForProficiency(it: String) : Boolean {
        proficiencies.forEach { prof ->
            if(prof.name?.lowercase() ?: "" == it.lowercase())
                return true
        }
        return false
    }

    fun checkForProficiencies(stats: List<String>): Map<String, Boolean> {
        val result = mutableMapOf<String, Boolean>()
        stats.forEach {
            result[it] = checkForProficiency(it)
        }
        return result
    }

    val totalCasterLevels : Int
    get() {
        var result = 0
        classes.values.forEach{
            result += floor(it.level.toDouble() * (it.spellCasting?.type ?: 0.0)).toInt()
        }
        return result
    }

    val hasBaseClass: Boolean
    get() {
        classes.forEach {
            if(it.value.isBaseClass) {
                return true
            }
        }
        return false
    }

    val proficiencyBonus : Int
    get() {
        return when(totalClassLevels) {
            in 1..4 -> 2
            in 5..8 -> 3
            in 9..12 -> 4
            in 13..16  -> 5
            in 17..20 -> 6
            else -> 0
        }
    }

    val passives : Map<String, Int>
        get() {
            return mapOf(
                "Passive Perception" to (10 + getStatMod("Wis") + proficiencyBonus),
                "Passive Investigation" to (10 + getStatMod("Int") + proficiencyBonus),
                "Passive Insight" to (10 + getStatMod("Wis") + proficiencyBonus)
            )
        }


    val languages: List<Language>
    get() {
        val result = mutableListOf<Language>()
        race?.languages?.let { result.addAll(it) }
        background?.languages?.let {result.addAll(it) }
        background?.languageChoices?.forEach { choice ->
            choice.chosen?.let { result.addAll(it) }
        }
        result.addAll(addedLanguages)
        return result
    }

    val proficiencies: List<Proficiency>
    get() {
        val result = mutableListOf<Proficiency>()
        background?.proficiencies?.let { result.addAll(it) }
        classes.forEach {
            result.addAll(it.value.proficiencies)
            it.value.proficiencyChoices.forEach { choice ->
                choice.chosen?.let { chosen -> result.addAll(chosen) }
            }
        }
        race?.startingProficiencies?.let { result.addAll(it) }
        result.addAll(addedProficiencies)
        return result
    }

    val isCaster : Boolean
    get() {
        if(totalCasterLevels != 0) {
            return true
        }
        return false
    }
    val allSpells: Map<Int, List<Spell>>
    get() {
        //TODO implement me
        return mapOf(
            1 to listOf(
                Spell("Firebolt", 1, listOf(), listOf(), "", "", "", "", "", "", listOf(), ""),
                Spell("Firebolt", 1, listOf(), listOf(), "", "", "", "", "", "", listOf(), ""),
                Spell("Firebolt", 1, listOf(), listOf(), "", "", "", "", "", "", listOf(), ""),
                Spell("Firebolt", 1, listOf(), listOf(), "", "", "", "", "", "", listOf(), "")
            )
        )
    }

}
