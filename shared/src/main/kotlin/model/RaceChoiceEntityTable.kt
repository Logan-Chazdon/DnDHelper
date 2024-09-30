package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.RaceChoiceEntity


/**This class is used to sync persisted choice data with updated race data for use in a character object.*/
@Entity(
    primaryKeys = ["raceId", "characterId"],
    tableName = "RaceChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = RaceEntityTable::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class RaceChoiceEntityTable(
    raceId: Int = 0,
    characterId: Int = 0,
    @ColumnInfo(name = "abcchosenByString")
    override var abilityBonusChoice: List<String> = emptyList(),
    proficiencyChoice: List<List<String>> = emptyList(),
    languageChoice: List<List<String>> = emptyList(),
    abilityBonusOverrides: List<AbilityBonus>? = null
) : RaceChoiceEntity(
    raceId,
    characterId,
    abilityBonusChoice,
    proficiencyChoice,
    languageChoice,
    abilityBonusOverrides
)

fun RaceChoiceEntity.asTable() : RaceChoiceEntityTable {
    return RaceChoiceEntityTable(
        raceId = raceId,
        characterId = characterId,
        abilityBonusChoice = abilityBonusChoice,
        proficiencyChoice = proficiencyChoice,
        languageChoice = languageChoice,
        abilityBonusOverrides = abilityBonusOverrides
    )
}