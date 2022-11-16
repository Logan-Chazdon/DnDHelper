package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity


//This is used to fetch a feature relating to a class.
@Entity(primaryKeys = ["id", "featureId"])
data class ClassFeatureCrossRef(
    val featureId: Int,
    val id: Int
)