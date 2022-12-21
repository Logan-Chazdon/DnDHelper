package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "backgroundId"])
data class BackgroundChoiceEntity(
    val characterId: Int,
    val backgroundId: Int,
    val languageChoices : List<List<String>>
)
