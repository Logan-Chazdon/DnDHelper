package model.choiceEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE


/**This class is used to sync persisted choice data with updated race data for use in a character object.*/
@Entity(
    primaryKeys = ["raceId", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = model.RaceEntity::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class RaceChoiceEntity(
    val raceId: Int = 0,
    val characterId: Int = 0,
    @ColumnInfo(name = "abcchosenByString")
    var abilityBonusChoice: List<String> = emptyList(),
    var proficiencyChoice: List<List<String>> = emptyList(),
    var languageChoice: List<List<String>> = emptyList(),
    var abilityBonusOverrides: List<model.AbilityBonus>? = null
)