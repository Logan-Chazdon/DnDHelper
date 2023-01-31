package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["choiceId", "index"])
data class FeatureChoiceIndexCrossRef(
    val choiceId: Int,
    val index: String
)
