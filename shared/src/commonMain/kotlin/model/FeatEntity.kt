package model


open class FeatEntity(
    val name: String,
    val desc: String,
    val prerequisite: Prerequisite? = null,
    val abilityBonuses: List<AbilityBonus>? = null,
    val abilityBonusChoice: AbilityBonusChoice? = null
) {
    open var id = 0
}