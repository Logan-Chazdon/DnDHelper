package com.example.dndhelper.repository.dataClasses

data class AbilityBonus(
    val ability: Ability,
    val bonus: Int,
) {
    override fun toString(): String {
        return "+$bonus $ability"
    }
}