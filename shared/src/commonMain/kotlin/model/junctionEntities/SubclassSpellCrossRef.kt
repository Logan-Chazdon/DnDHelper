package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["subclassId", "spellId"],
    foreignKeys = [
        ForeignKey(
            entity = model.SubclassEntity::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
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
data class SubclassSpellCrossRef(
    val subclassId : Int,
    val spellId: Int
)
