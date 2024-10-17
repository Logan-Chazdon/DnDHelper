package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["subraceId", "featChoiceId"],
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntityTable::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatChoiceEntityTable::class,
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