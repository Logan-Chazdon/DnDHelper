package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["backgroundId", "featureId"])
data class BackgroundFeatureCrossRef(
    val backgroundId : Int,
    val featureId: Int
)
