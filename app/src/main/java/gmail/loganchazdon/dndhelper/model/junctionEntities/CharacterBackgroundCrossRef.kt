package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "backgroundId"])
data class CharacterBackgroundCrossRef(
    val characterId: Int,
    val backgroundId: Int
)