package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import gmail.loganchazdon.dndhelper.model.FeatEntity
import gmail.loganchazdon.dndhelper.model.FeatureEntity


@Entity(
    primaryKeys = ["featureId", "featId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatEntity::class,
            childColumns = ["featId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ]
)
data class FeatFeatureCrossRef(
    val featId: Int,
    val featureId: Int
)
