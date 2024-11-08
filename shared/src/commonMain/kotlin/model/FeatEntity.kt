package model

import kotlinx.serialization.Serializable


@Serializable
open class FeatEntity(
    var name: String,
    var desc: String,
    var prerequisite: Prerequisite? = null,
    var abilityBonuses: List<AbilityBonus>? = null,
    var abilityBonusChoice: AbilityBonusChoice? = null
) {
    open var id = 0
}