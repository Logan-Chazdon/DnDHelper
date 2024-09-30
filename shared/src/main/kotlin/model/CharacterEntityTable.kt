package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import model.database.converters.Converters

@Entity(tableName="characters")
@TypeConverters(Converters::class)
class CharacterEntityTable : CharacterEntity(){
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0
}

fun CharacterEntity.asTable() : CharacterEntityTable {
    return CharacterEntityTable().run {
        name = this@asTable.name
        personalityTraits = this@asTable.personalityTraits
        ideals = this@asTable.ideals
        bonds = this@asTable.bonds
        flaws = this@asTable.flaws
        notes = this@asTable.notes
        currentHp = this@asTable.currentHp
        tempHp = this@asTable.tempHp
        conditions = this@asTable.conditions
        resistances = this@asTable.resistances
        id = this@asTable.id
        statGenerationMethodIndex = this@asTable.statGenerationMethodIndex
        baseStats = this@asTable.baseStats
        backpack = this@asTable.backpack
        inspiration = this@asTable.inspiration
        positiveDeathSaves = this@asTable.positiveDeathSaves
        negativeDeathSaves = this@asTable.negativeDeathSaves
        spellSlots = this@asTable.spellSlots
        addedLanguages = this@asTable.addedLanguages
        addedProficiencies = this@asTable.addedProficiencies
        this
    }
}


