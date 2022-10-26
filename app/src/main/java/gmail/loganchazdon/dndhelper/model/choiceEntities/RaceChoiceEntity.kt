package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import gmail.loganchazdon.dndhelper.model.AbilityBonus


//This class is used to sync persisted choice data with updated race data for use in a character object.
@Entity(primaryKeys = ["raceId", "characterId"])
data class RaceChoiceEntity(
    val raceId : Int = 0,
    val characterId: Int = 0,
    @ColumnInfo(name = "abcchosen")
    var chosen: List<AbilityBonus>? = null
)