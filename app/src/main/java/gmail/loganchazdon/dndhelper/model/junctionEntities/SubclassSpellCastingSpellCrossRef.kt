package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity


@Entity(primaryKeys = ["subclassId", "spellId", "characterId"])
data class SubclassSpellCastingSpellCrossRef(
    val subclassId: Int,
    val spellId: Int,
    val characterId: Int,
    val isPrepared: Boolean?
)
