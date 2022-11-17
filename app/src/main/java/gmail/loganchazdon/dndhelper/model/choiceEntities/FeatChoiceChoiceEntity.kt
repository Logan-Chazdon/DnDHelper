package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId","choiceId","featureId"])
class FeatChoiceChoiceEntity(
    val characterId: Int,
    val choiceId: Int,
    val featureId: Int,
)