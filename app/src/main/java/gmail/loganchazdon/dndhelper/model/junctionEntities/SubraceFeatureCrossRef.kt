package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity


@Entity(primaryKeys = ["subraceId", "featureId"])
data class SubraceFeatureCrossRef(
    val subraceId: Int,
    val featureId: Int
)