package com.example.dndhelper.repository.dataClasses

data class AbilityBonus(
    val ability: String,
    val bonus: Int,
) {
    override fun toString(): String {
        return "+$bonus $ability"
    }
}