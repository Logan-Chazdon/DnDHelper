package model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE


@Entity(
    primaryKeys = ["subclassId", "spellId", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = model.SubclassEntity::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.Spell::class,
            childColumns = ["spellId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class SubclassSpellCastingSpellCrossRef(
    val subclassId: Int,
    val spellId: Int,
    val characterId: Int,
    val isPrepared: Boolean?
)
