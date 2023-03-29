package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.BackgroundEntity
import gmail.loganchazdon.dndhelper.model.CharacterEntity

@Entity(
    primaryKeys = ["characterId", "backgroundId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = BackgroundEntity::class,
            childColumns = ["backgroundId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterBackgroundCrossRef(
    val characterId: Int,
    val backgroundId: Int
)