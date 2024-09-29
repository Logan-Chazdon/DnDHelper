package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["backgroundId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = model.BackgroundEntity::class,
            childColumns = ["backgroundId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = model.FeatureEntity::class,
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
