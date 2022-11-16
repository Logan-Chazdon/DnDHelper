package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity

//This is used to fetch all features in the chosen field of a feature choice.
@Entity(primaryKeys = ["featureId", "characterId", "choiceId"])
data class FeatureChoiceChoiceEntity (
    val featureId: Int = 0,
    val characterId: Int = 0,
    val choiceId: Int = 0
)