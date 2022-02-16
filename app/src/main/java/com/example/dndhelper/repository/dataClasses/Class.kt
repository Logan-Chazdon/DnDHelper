package com.example.dndhelper.repository.dataClasses

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="classes")
@TypeConverters(Converters::class)
class Class(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "name")
    var name: String,
    var hitDie: Int = 8,
    var subClasses: List<Subclass> = emptyList(),
    var subclassLevel: Int,
    var levelPath: MutableList<Feature> = mutableListOf<Feature>(),
    var proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    var proficiencies: List<Proficiency> = emptyList(),
    var equipmentChoices: List<ItemChoice> = emptyList(),
    var equipment: List<ItemInterface> = emptyList(),
    val spellCasting : SpellCasting? = null,
    val startingGoldD4s: Int
) {
    var isBaseClass: Boolean = false
    var level: Int = 1
    var subclass: Subclass? = null

    fun longRest() {
        levelPath.forEach{
            it.recharge(level)
        }
    }

    //TODO implement me.
    //Return all spells available through this class
    //Regardless of if they are prepared.
    fun getAvailableSpells() : List<Spell> {
        val result = mutableListOf<Spell>()
        spellCasting?.known?.let { result.addAll(it) }
        for (it in levelPath) {
            if(it.grantedAtLevel > level) {
                break
            }
            result.addAll(it.getSpellsGiven())
        }
        return result
    }
}
