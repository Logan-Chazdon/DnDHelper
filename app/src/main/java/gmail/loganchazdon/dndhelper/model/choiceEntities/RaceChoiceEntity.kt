package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.ColumnInfo
import androidx.room.Entity


//This class is used to sync persisted choice data with updated race data for use in a character object.
@Entity(primaryKeys = ["raceId", "characterId"])
data class RaceChoiceEntity(
    val raceId: Int = 0,
    val characterId: Int = 0,
    @ColumnInfo(name = "abcchosenByString")
    var abilityBonusChoice: List<String> = emptyList(),
    var proficiencyChoice: List<List<String>> = emptyList(),
    var languageChoice: List<List<String>> = emptyList()
)