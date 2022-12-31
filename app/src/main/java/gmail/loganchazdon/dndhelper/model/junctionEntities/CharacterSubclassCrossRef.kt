package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subClassId", "characterId", "classId"])
data class CharacterSubclassCrossRef(
    val subClassId: Int,
    val characterId: Int,
    val classId: Int
)