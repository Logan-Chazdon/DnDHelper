package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.SubraceChoiceEntity


@Entity(
    primaryKeys = ["subraceId", "characterId"],
    tableName = "SubraceChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntityTable::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
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
class SubraceChoiceEntityTable(
    subraceId : Int,
    characterId: Int,
    languageChoice: List<List<String>> = emptyList(),
    @ColumnInfo(name = "abcchosenByString")
    override var abilityBonusChoice: List<String> = emptyList(),
    abilityBonusOverrides: List<AbilityBonus>? = null
) : SubraceChoiceEntity(
    subraceId,
    characterId,
    languageChoice,
    abilityBonusChoice,
    abilityBonusOverrides
)
