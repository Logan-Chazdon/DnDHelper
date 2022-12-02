package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "classId"])
data class CharacterClassCrossRef(
    val characterId: Int,
    val classId: Int
)
