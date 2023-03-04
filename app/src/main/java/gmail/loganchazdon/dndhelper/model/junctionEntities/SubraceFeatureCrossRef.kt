package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureEntity
import gmail.loganchazdon.dndhelper.model.SubraceEntity


@Entity(
    primaryKeys = ["subraceId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntity::class,
            childColumns = ["subraceId"],
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
data class SubraceFeatureCrossRef(
    val subraceId: Int,
    val featureId: Int
)