package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["featChoiceId", "featId"])
data class FeatChoiceFeatCrossRef(
    val featChoiceId: Int,
    val featId: Int
)
