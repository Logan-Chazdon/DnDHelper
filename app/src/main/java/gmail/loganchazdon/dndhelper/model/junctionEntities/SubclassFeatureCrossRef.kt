package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subclassId", "featureId"])
data class SubclassFeatureCrossRef(
    val subclassId: Int,
    val featureId: Int
)