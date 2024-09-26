package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["subClassId", "characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = model.SubclassEntity::class,
            childColumns = ["subClassId"],
            parentColumns = ["subclassId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
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
data class CharacterSubclassCrossRef(
    val subClassId: Int,
    val characterId: Int,
    val classId: Int
)