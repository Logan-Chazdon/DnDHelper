package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["choiceId", "index"],
    foreignKeys = [
        ForeignKey(
            entity = model.FeatureChoiceEntity::class,
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
