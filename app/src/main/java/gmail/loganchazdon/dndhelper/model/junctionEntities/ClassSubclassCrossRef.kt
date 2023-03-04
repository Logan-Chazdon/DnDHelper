package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.ClassEntity
import gmail.loganchazdon.dndhelper.model.SubclassEntity

@Entity(
    primaryKeys = ["classId", "subclassId"],
    foreignKeys = [
        ForeignKey(
            entity = ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = SubclassEntity::class,
            childColumns = ["subclassId"],
            parentColumns = ["subclassId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class ClassSubclassCrossRef(
    val classId: Int,
    val subclassId: Int
)