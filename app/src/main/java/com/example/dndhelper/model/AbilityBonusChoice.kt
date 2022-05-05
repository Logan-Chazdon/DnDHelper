package com.example.dndhelper.model

data class AbilityBonusChoice(
    val choose: Int,
    val from : List<AbilityBonus>,
    val maxOccurrencesOfAbility: Int = 1
) {
    var chosen: List<AbilityBonus>? = null

    companion object {
        val allStatsArray = listOf(
            AbilityBonus("Strength", 1),
            AbilityBonus("Constitution", 1),
            AbilityBonus("Dexterity", 1),
            AbilityBonus("Intelligence", 1),
            AbilityBonus("Wisdom", 1),
            AbilityBonus("Charisma", 1),
        )
    }
}
