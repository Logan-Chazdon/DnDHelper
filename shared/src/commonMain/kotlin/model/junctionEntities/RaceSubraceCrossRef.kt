package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["subraceId", "raceId"],
    foreignKeys = [
        ForeignKey(
            entity = model.SubraceEntity::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = model.RaceEntity::class,
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