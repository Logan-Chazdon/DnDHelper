package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.FeatureEntity
import gmail.loganchazdon.dndhelper.model.Spell


@Entity(
    primaryKeys = ["spellId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = Spell::class,
            childColumns = ["spellId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = true
        )
    ]
)
data class FeatureSpellCrossRef(
    val spellId: Int,
    val featureId: Int
)
