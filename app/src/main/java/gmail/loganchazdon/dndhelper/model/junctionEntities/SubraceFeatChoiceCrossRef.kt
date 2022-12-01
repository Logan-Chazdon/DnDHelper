package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["subraceId", "featChoiceId"])
data class SubraceFeatChoiceCrossRef(
    val subraceId: Int,
    val featChoiceId: Int
)