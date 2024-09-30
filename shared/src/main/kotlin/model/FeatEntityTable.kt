package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feats")
class FeatEntityTable(
    name: String,
    desc: String,
    prerequisite: Prerequisite? = null,
    abilityBonuses: List<AbilityBonus>? = null,
    abilityBonusChoice: AbilityBonusChoice? = null
) : FeatEntity(
    name,
    desc,
    prerequisite,
    abilityBonuses,
    abilityBonusChoice
) {
    @PrimaryKey(autoGenerate = true)
    override var id = 0
}

fun FeatEntity.asTable(): FeatEntityTable {
    return FeatEntityTable(
        name,
        desc,
        prerequisite,
        abilityBonuses,
        abilityBonusChoice
    ).run { id = this@asTable.id; this }
}