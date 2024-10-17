package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["subclassId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = SubclassEntityTable::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
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
data class SubclassFeatureCrossRef(
    val subclassId: Int,
    val featureId: Int
)