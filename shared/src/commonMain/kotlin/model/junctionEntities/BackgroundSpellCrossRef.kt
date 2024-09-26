package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["backgroundId", "spellId"],
    foreignKeys = [
        ForeignKey(
            entity = model.BackgroundEntity::class,
            childColumns = ["backgroundId"],
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
data class BackgroundSpellCrossRef(
    val backgroundId : Int,
    val spellId: Int
)