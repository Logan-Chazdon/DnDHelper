package gmail.loganchazdon.dndhelper.model.stateEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.ClassEntity

@Entity(
    primaryKeys = ["characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PactMagicStateEntity(
    val characterId: Int,
    val classId: Int,
    val slotsCurrentAmount: Int
)