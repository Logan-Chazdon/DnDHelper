package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.BackgroundEntity
import gmail.loganchazdon.dndhelper.model.FeatureEntity

@Entity(
    primaryKeys = ["backgroundId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = BackgroundEntity::class,
            childColumns = ["backgroundId"],
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
data class BackgroundFeatureCrossRef(
    val backgroundId : Int,
    val featureId: Int
)
