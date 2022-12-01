package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subraceId", "characterId"])
data class CharacterSubraceCrossRef(
    val subraceId: Int,
    val characterId: Int
)
