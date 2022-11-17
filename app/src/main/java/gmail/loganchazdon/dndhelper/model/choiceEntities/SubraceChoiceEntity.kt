package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(primaryKeys = ["subraceId", "characterId"])
data class SubraceChoiceEntity(
    val subraceId : Int,
    val characterId: Int,
    var languageChoice: List<List<String>> = emptyList(),
    @ColumnInfo(name = "abcchosenByString")
    var abilityBonusChoice: List<String> = emptyList(),
)
