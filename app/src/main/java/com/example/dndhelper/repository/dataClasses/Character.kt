package com.example.dndhelper.repository.dataClasses


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.dataClasses.utils.getValueInCopper
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="characters")
@TypeConverters(Converters::class)
data class Character(
    @ColumnInfo(name="name")
    var name: String,
    var race: Race? = null
){
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0

    var baseStats = mutableMapOf<String, Int>()
    var abilityScoreIncreases: MutableMap<String, Int> = mutableMapOf(
        "Strength" to 0,
        "Dexterity" to 0,
        "Constitution" to 0,
        "Intelligence" to 0,
        "Wisdom" to 0,
        "Charisma" to 0
    )


    var currentHp: Int = 0
    var tempHp: Int = 0
    var maxHp: Int = 0
    fun generateMaxHp(): Int {
        var newMax = 0
        for(item in classes) {
            newMax += (((item.hitDie / 2) + 1) + getStatMod("Con")) * item.level
        }
        return newMax
    }

    var classes = mutableListOf<Class>()
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

    var background: Background? = null


    var backpack = Backpack()

    var inspiration = false


    var equiptArmor = Armor.none

    var feats = mutableListOf<Feat>()

    fun getStats(): MutableMap<String, Int> {
        return baseStats  //TODO add bonuses and such
    }

    fun getStat(name: String): Int? {
        return baseStats[name] //TODO add bonuses and such
    }

    fun getStatMod(name: String): Int {
        return (getStat(name)!! - 10) / 2
    }

    fun addAbilityScoreIncreases(increases: Map<String, Int>) {
        for(item in increases) {
            abilityScoreIncreases[item.key] = abilityScoreIncreases[item.key]?.plus(item.value) ?: 0
        }
    }

}
