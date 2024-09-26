package model.choiceEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

/**This is used to fetch all features in the chosen field of a feature choice.*/
@Entity(
    primaryKeys = ["featureId", "characterId", "choiceId"],
    foreignKeys = [
        ForeignKey(
            entity = model.FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.FeatureChoiceEntity::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class FeatureChoiceChoiceEntity (
    val featureId: Int = 0,
    val characterId: Int = 0,
    val choiceId: Int = 0
)