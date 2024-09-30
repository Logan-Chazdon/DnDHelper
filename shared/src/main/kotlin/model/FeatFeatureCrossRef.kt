package model

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    primaryKeys = ["featureId", "featId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntityTable::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatEntityTable::class,
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
