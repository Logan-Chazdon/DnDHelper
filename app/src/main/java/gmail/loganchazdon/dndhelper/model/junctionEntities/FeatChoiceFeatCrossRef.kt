package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatChoiceEntity
import gmail.loganchazdon.dndhelper.model.FeatEntity

@Entity(
    primaryKeys = ["featChoiceId", "featId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatChoiceEntity::class,
            childColumns = ["featChoiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatEntity::class,
            childColumns = ["featId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class FeatChoiceFeatCrossRef(
    val featChoiceId: Int,
    val featId: Int
)
