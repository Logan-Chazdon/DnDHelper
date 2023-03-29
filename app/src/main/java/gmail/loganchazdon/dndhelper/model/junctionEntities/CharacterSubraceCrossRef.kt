package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.SubraceEntity

@Entity(
    primaryKeys = ["subraceId", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntity::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterSubraceCrossRef(
    val subraceId: Int,
    val characterId: Int
)
