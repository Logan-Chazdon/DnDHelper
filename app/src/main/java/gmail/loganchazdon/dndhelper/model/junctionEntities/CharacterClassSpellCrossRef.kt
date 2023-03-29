package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.ClassEntity
import gmail.loganchazdon.dndhelper.model.Spell


@Entity(
    primaryKeys = ["characterId", "classId",  "spellId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Spell::class,
            childColumns = ["spellId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterClassSpellCrossRef(
    val characterId: Int,
    val classId: Int,
    val spellId: Int,
    val isPrepared: Boolean?
)
