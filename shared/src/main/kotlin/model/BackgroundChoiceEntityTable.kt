package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.BackgroundChoiceEntity

@Entity(
    primaryKeys = ["characterId", "backgroundId"],
    tableName = "BackgroundChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = BackgroundEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class BackgroundChoiceEntityTable(
    characterId: Int,
    backgroundId: Int,
    languageChoices : List<List<String>>
) : BackgroundChoiceEntity(
    characterId,
    backgroundId,
    languageChoices
)


fun BackgroundChoiceEntity.asTable() : BackgroundChoiceEntityTable {
    return BackgroundChoiceEntityTable(characterId, backgroundId, languageChoices)
}