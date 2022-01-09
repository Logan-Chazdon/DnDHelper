package com.example.dndhelper.repository.dataClasses

data class Feat(
    val name : String,
    val desc: String,
    val prerequisite: String? = null,
    val features : List<Feature>? = null,
    val abilityBonuses: AbilityBonusChoice? = null,
    val spell: List<SpellChoice>,
)
