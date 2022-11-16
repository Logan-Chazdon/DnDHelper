package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

//This is used to fetch all features relating to a race.
@Entity(primaryKeys = ["raceId", "featureId"])
data class RaceFeatureCrossRef(
    val featureId: Int,
    val raceId: Int
)