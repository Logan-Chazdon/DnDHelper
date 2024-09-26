package model.stateEntities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = model.ClassEntity::class,
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