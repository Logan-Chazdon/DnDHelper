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
    var race: Race? = null,
    var currentHp: Int = 0,
    var tempHp: Int = 0,
    var maxHp: Int = 0,
    var conditions: MutableList<String> = mutableListOf<String>(),
    var resistances: MutableList<String> = mutableListOf<String>(),
    var classes: MutableList<Class> = mutableListOf<Class>(),
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0,
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
    var spellSlots: List<Resource> = listOf()
){
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


    fun generateMaxHp(): Int {
        var newMax = 0
        for(item in classes) {
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
        for(item in classes) {
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
        classes.add(newClass)


        spellSlots = when(totalCasterLevels) {
            1 -> {
                listOf(Resource(name = "1st", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"))
            }
            else -> {
                listOf()
            }

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
        for((i, item) in classes.withIndex()) {
            result += "${item.name} ${item.level}"
            if(i != classes.size - 1) {
                result += ", "
            }
        }
        return result
    }

    fun longRest() {
        //Classes
        classes.forEach {
            it.longRest()
        }

        //Race
        race?.longRest()

        //Items

        //Hp
        tempHp = 0
        currentHp = maxHp
    }

    val totalCasterLevels : Int
    get() {
        var result = 0
        classes.forEach{
            result += floor(it.level.toDouble() * it.spellCasting).toInt()
        }
        return result
    }
}
