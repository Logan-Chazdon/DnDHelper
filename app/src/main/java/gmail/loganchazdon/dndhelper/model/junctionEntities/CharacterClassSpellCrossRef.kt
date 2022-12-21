package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "classId", "spellId"])
data class CharacterClassSpellCrossRef(
    val characterId: Int,
    val classId: Int,
    val spellId: Int,
    val isPrepared: Boolean?
)
