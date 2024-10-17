package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

/**This is used to fetch all features which belong in the options field of a featureChoice.*/
@Entity(
    primaryKeys = ["featureId", "choiceId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureEntityTable::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatureChoiceEntityTable::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class OptionsFeatureCrossRef(
    val featureId: Int,
    val choiceId: Int,
)