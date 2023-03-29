package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.RaceEntity

/**This is used to fetch a race relating to a character.*/
@Entity(
    primaryKeys = ["raceId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = RaceEntity::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["id"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterRaceCrossRef(
    val raceId: Int,
    val id: Int
)