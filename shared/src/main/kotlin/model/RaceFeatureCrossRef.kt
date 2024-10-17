package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

/**This is used to fetch all features relating to a race.*/
@Entity(
    primaryKeys = ["featureId", "raceId"],
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
            entity = RaceEntityTable::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class RaceFeatureCrossRef(
    val featureId: Int,
    val raceId: Int
)