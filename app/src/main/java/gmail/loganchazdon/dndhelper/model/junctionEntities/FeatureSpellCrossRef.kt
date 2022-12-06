package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity


@Entity(primaryKeys = ["spellId", "featureId"])
data class FeatureSpellCrossRef(
    val spellId: Int,
    val featureId: Int
)
