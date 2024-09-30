package model


data class AbilityBonusChoice(
    val choose: Int,
    val from : List<AbilityBonus>,
    val maxOccurrencesOfAbility: Int = 1
) {
    var chosenByString: List<String> = ArrayList()
    val chosen: List<AbilityBonus>
    get() {
        return from.filter {
            chosenByString.contains(it.toString())
        }
    }

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
