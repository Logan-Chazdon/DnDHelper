package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.FeatureChoiceChoiceEntity

/**This is used to fetch all features in the chosen field of a feature choice.*/
@Entity(
    primaryKeys = ["featureId", "characterId", "choiceId"],
    tableName = "FeatureChoiceChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntityTable::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = FeatureChoiceEntityTable::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class FeatureChoiceChoiceEntityTable(
    featureId: Int = 0,
    characterId: Int = 0,
    choiceId: Int = 0
) : FeatureChoiceChoiceEntity(
    featureId,
    characterId,
    choiceId
)