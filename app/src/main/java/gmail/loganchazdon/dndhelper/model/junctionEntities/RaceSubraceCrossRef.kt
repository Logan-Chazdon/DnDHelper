package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subraceId", "raceId"])
data class RaceSubraceCrossRef(
    val subraceId: Int,
    val raceId: Int
)