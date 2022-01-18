package com.example.dndhelper.repository.dataClasses

data class SpellCasting (
    val type : Double, //1 for full casters, 0.5 for half casters.
    val castingAbility: String,
    val prepareFrom: String?,
    val preparationModMultiplier : Double?,
    val spellsKnown: List<Int>?,
    val cantripsKnown: List<Int>?,
    val prepared: MutableList<Spell> = mutableListOf(),
    val known: MutableList<Spell> = mutableListOf()
) {

}
