package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

@Entity(primaryKeys = ["classId", "subclassId"])
data class ClassSubclassCrossRef(
    val classId: Int,
    val subclassId: Int
)