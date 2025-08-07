package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import kotlinx.serialization.Serializable

/**This is used to fetch all featureChoices relating to a feature.*/
@Entity(
    primaryKeys = ["id", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureChoiceEntityTable::class,
            childColumns = ["id"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatureEntityTable::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
@Serializable
data class FeatureOptionsCrossRef(
    val featureId: Int,
    val id: Int
)