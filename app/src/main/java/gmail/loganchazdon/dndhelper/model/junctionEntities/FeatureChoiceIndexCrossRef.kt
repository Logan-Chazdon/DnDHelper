package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity

@Entity(
    primaryKeys = ["choiceId", "index"],
    foreignKeys = [
        ForeignKey(
            entity = FeatureChoiceEntity::class,
            childColumns = ["choiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class FeatureChoiceIndexCrossRef(
    val choiceId: Int,
    val index: String,
    var levels:  List<Int>? = null,
    val classes:  List<String>? = null,
    val schools:  List<String>? = null,
)
