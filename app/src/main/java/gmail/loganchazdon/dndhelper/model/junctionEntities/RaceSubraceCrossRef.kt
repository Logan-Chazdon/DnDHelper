package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.RaceEntity
import gmail.loganchazdon.dndhelper.model.SubraceEntity

@Entity(
    primaryKeys = ["subraceId", "raceId"],
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
            entity = RaceEntity::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class RaceSubraceCrossRef(
    val subraceId: Int,
    val raceId: Int
)