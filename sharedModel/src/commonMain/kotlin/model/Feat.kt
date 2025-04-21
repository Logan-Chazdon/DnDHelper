package model

import kotlinx.serialization.Serializable

@Serializable
class Feat() : FeatEntity("", "", null, null, null) {
    var features: List<Feature>? = null
    constructor(
        name: String,
        desc: String,
        prerequisite: Prerequisite? = null,
        features: List<Feature>? = null,
        abilityBonuses: List<AbilityBonus>? = null,
        abilityBonusChoice: AbilityBonusChoice? = null
    ) : this() {
        this.name = name
        this.desc =desc
        this.prerequisite = prerequisite
        this.features = features
        this.abilityBonuses = abilityBonuses
        this.abilityBonusChoice = abilityBonusChoice
    }
}