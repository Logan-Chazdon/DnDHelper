package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.FeatureEntity

/**This is used to fetch all featureChoices relating to a feature.*/
@Entity(
    primaryKeys = ["id", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureChoiceEntity::class,
            childColumns = ["id"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class FeatureOptionsCrossRef(
    val featureId: Int,
    val id: Int
)