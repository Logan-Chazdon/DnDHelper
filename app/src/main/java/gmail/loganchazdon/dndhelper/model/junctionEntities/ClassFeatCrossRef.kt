package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "classId", "featId"])
data class ClassFeatCrossRef(
    val characterId : Int,
    val classId: Int,
    val featId : Int
)
