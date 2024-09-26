package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterClassCrossRef(
    val characterId: Int,
    val classId: Int
)
