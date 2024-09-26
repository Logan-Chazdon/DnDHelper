package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["classId", "spellId"],
    foreignKeys = [
        ForeignKey(
            entity = model.ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = model.Spell::class,
            childColumns = ["spellId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class ClassSpellCrossRef(
    val classId: Int,
    val spellId: Int
)
