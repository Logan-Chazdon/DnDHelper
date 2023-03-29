package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feats")
open class FeatEntity(
    val name: String,
    val desc: String,
    val prerequisite: Prerequisite? = null,
    val abilityBonuses: List<AbilityBonus>? = null,
    val abilityBonusChoice: AbilityBonusChoice? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}