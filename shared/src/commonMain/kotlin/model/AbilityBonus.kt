package model

import kotlinx.serialization.Serializable

@Serializable
data class AbilityBonus(
    val ability: String,
    val bonus: Int,
) {
    override fun toString(): String {
        return "+$bonus $ability"
    }
}