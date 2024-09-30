package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.FeatChoiceChoiceEntity

@Entity(
    primaryKeys = ["characterId", "choiceId", "featId"],
    tableName = "FeatChoiceChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = FeatChoiceEntityTable::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = FeatEntityTable::class,
            childColumns = ["featId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class FeatChoiceChoiceEntityTable(
    characterId: Int,
    choiceId: Int,
    featId: Int,
) : FeatChoiceChoiceEntity(
    characterId,
    choiceId,
    featId
)