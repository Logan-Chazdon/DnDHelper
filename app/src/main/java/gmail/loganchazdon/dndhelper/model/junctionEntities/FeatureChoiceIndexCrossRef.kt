package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["choiceId", "index"])
data class FeatureChoiceIndexCrossRef(
    val choiceId: Int,
    val index: String,
    var levels:  List<Int>? = null,
    val classes:  List<String>? = null,
    val schools:  List<String>? = null,
)
