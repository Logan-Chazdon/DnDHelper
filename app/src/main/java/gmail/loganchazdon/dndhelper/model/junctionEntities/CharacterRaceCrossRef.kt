package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters

@TypeConverters(Converters::class)
@Entity(primaryKeys = ["id", "raceId"])
data class CharacterRaceCrossRef(
    val raceId: Int,
    val id: Int
)