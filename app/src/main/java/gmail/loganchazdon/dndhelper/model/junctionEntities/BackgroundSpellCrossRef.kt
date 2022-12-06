package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["backgroundId", "spellId"])
data class BackgroundSpellCrossRef(
    val backgroundId : Int,
    val spellId: Int
)