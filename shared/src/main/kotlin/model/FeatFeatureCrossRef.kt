package model

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable


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
@Serializable
data class FeatFeatureCrossRef(
    val featId: Int,
    val featureId: Int
)
