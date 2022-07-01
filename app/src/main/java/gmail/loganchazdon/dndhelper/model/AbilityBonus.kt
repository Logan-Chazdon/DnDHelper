package gmail.loganchazdon.dndhelper.model

data class AbilityBonus(
    val ability: String,
    val bonus: Int,
) {
    override fun toString(): String {
        return "+$bonus $ability"
    }
}