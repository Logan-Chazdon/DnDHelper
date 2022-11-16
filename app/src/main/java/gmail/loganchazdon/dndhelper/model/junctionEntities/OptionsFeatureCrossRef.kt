package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

//This is used to fetch all features which belong in the options field of a featureChoice.
@Entity(primaryKeys = ["featureId", "choiceId"])
data class OptionsFeatureCrossRef(
    val featureId: Int,
    val choiceId: Int,
)