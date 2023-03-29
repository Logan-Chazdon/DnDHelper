package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.FeatureEntity

/**This is used to fetch all features which belong in the options field of a featureChoice.*/
@Entity(
    primaryKeys = ["featureId", "choiceId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatureChoiceEntity::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class OptionsFeatureCrossRef(
    val featureId: Int,
    val choiceId: Int,
)