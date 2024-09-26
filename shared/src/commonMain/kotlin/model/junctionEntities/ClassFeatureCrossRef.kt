package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE


/**This is used to fetch a feature relating to a class.*/
@Entity(
    primaryKeys = ["featureId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = model.FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = model.ClassEntity::class,
            childColumns = ["id"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class ClassFeatureCrossRef(
    val featureId: Int,
    val id: Int
)