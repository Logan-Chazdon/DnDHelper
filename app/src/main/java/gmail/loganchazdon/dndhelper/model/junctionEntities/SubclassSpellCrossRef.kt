package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subclassId", "spellId"])
data class SubclassSpellCrossRef(
    val subclassId : Int,
    val spellId: Int
)
