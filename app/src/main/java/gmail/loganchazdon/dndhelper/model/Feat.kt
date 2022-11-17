package gmail.loganchazdon.dndhelper.model

class Feat(
    name: String,
    desc: String,
    prerequisite: Prerequisite? = null,
    var features: List<Feature>? = null,
    abilityBonuses: List<AbilityBonus>? = null,
    abilityBonusChoice: AbilityBonusChoice? = null
) : FeatEntity(name, desc, prerequisite, abilityBonuses, abilityBonusChoice)
