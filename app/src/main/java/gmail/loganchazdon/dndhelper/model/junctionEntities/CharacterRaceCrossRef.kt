package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity

//This is used to fetch a race relating to a character.
@Entity(primaryKeys = ["id", "raceId"])
data class CharacterRaceCrossRef(
    val raceId: Int,
    val id: Int
)