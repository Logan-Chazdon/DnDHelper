package model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClassEntityTable::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PactMagicStateEntity(
    val characterId: Int,
    val classId: Int,
    val slotsCurrentAmount: Int
)