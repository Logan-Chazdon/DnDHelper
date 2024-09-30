package model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import model.database.converters.Converters


@Entity(tableName="races")
@TypeConverters(Converters::class)
class RaceEntityTable : RaceEntity() {
    @PrimaryKey(autoGenerate = true)
    override var raceId: Int = 0
    @Embedded(prefix = "abc")
    override var abilityBonusChoice: AbilityBonusChoice? = null
}

fun RaceEntity.asTable() : RaceEntityTable {
    return RaceEntityTable().run {
        raceId = this@asTable.raceId
        raceName = this@asTable.raceName
        groundSpeed = this@asTable.groundSpeed
        abilityBonuses = this@asTable.abilityBonuses
        abilityBonusChoice = this@asTable.abilityBonusChoice
        alignment = this@asTable.alignment
        age  = this@asTable.age
        size = this@asTable.size
        sizeDesc = this@asTable.sizeDesc
        startingProficiencies = this@asTable.startingProficiencies
        proficiencyChoices  = this@asTable.proficiencyChoices
        languages = this@asTable.languages
        languageChoices = this@asTable.languageChoices
        languageDesc = this@asTable.languageDesc
        isHomebrew = this@asTable.isHomebrew
        this
    }
}