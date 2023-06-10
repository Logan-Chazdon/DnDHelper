package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.FeatureEntity

/**This is used to fetch all features in the chosen field of a feature choice.*/
@Entity(
    primaryKeys = ["featureId", "characterId", "choiceId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = FeatureChoiceEntity::class,
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