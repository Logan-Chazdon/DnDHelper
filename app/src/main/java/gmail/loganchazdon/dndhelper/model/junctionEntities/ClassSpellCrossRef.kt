package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["classId", "spellId"])
data class ClassSpellCrossRef(
    val classId: Int,
    val spellId: Int
)
