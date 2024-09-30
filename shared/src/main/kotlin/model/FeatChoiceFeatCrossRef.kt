package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["featChoiceId", "featId"],
    foreignKeys = [
        ForeignKey(
            entity = FeatChoiceEntityTable::class,
            childColumns = ["featChoiceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatEntityTable::class,
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
