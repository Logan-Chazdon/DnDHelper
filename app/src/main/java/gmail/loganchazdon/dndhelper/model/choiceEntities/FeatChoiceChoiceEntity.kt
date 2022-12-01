package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId","choiceId","featId"])
class FeatChoiceChoiceEntity(
    val characterId: Int,
    val choiceId: Int,
    val featId: Int,
)