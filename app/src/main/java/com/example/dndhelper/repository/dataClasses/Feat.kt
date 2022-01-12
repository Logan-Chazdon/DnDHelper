package com.example.dndhelper.repository.dataClasses

data class Feat(
    val name: String,
    val desc: String,
    val prerequisite: String? = null,
    val features: List<Feature>? = null,
    val abilityBonus: AbilityBonus? = null,
    val abilityBonusChoice: AbilityBonusChoice? = null,
    val spells: List<Spell>,
    val spellChoices: List<SpellChoice>,
)
