package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import kotlinx.serialization.Serializable

@Entity(
    primaryKeys = ["subclassId", "spellId"],
    foreignKeys = [
        ForeignKey(
            entity = SubclassEntityTable::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = SpellTable::class,
            childColumns = ["spellId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
@Serializable
data class SubclassSpellCrossRef(
    val subclassId : Int,
    val spellId: Int
)
