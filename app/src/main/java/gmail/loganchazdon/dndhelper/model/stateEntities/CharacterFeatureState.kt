package gmail.loganchazdon.dndhelper.model.stateEntities

import androidx.room.Entity


@Entity(primaryKeys = ["characterId", "featureId"])
data class CharacterFeatureState(
    val characterId: Int,
    val featureId : Int,
    val isActive: Boolean
)
