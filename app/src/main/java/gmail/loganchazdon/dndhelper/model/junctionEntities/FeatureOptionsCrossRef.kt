package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

//This is used to fetch all featureChoices relating to a feature.
@Entity(primaryKeys = ["id", "featureId"])
data class FeatureOptionsCrossRef(
    val featureId: Int,
    val id: Int
)