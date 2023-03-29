package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.ClassEntity
import gmail.loganchazdon.dndhelper.model.SubclassEntity

@Entity(
    primaryKeys = ["subClassId", "characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = SubclassEntity::class,
            childColumns = ["subClassId"],
            parentColumns = ["subclassId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterSubclassCrossRef(
    val subClassId: Int,
    val characterId: Int,
    val classId: Int
)