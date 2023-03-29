package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureEntity
import gmail.loganchazdon.dndhelper.model.SubclassEntity

@Entity(
    primaryKeys = ["subclassId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = SubclassEntity::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
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
data class SubclassFeatureCrossRef(
    val subclassId: Int,
    val featureId: Int
)