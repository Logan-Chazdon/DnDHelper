package gmail.loganchazdon.dndhelper.model

data class Feat(
    val name: String,
    val desc: String,
    val prerequisite: Prerequisite? = null,
    val features: List<Feature>? = null,
    val abilityBonuses: List<AbilityBonus>? = null,
    val abilityBonusChoice: AbilityBonusChoice? = null
)
