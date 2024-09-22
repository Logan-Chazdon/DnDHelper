package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatChoiceEntity
import gmail.loganchazdon.dndhelper.model.SubraceEntity

@Entity(
    primaryKeys = ["subraceId", "featChoiceId"],
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntity::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatChoiceEntity::class,
            childColumns = ["featChoiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class SubraceFeatChoiceCrossRef(
    val subraceId: Int,
    val featChoiceId: Int
)