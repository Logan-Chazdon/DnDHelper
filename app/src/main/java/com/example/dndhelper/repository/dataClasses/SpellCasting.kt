package com.example.dndhelper.repository.dataClasses

import kotlin.math.floor

data class SpellCasting (
    val type : Double, //1 for full casters, 0.5 for half casters.
    val hasSpellBook: Boolean?,
    val castingAbility: String,
    val prepareFrom: String?,
    val preparationModMultiplier : Double?,
    val spellsKnown: List<Int>?,
    val cantripsKnown: List<Int>?,
    val prepared: MutableList<Spell> = mutableListOf(),
    val known: MutableList<Spell> = mutableListOf(),
    val spellSlotsByLevel: List<List<Resource>>? = null,
    val learnFrom: List<String>? = null, //Only used when the list of spells to present is not implied.
    val schoolRestriction : SchoolRestriction? = null
) {
    fun getMaxPrepared(classLevel: Int, castingMod: Int) : Int {
        return castingMod +  floor((classLevel.toDouble() * preparationModMultiplier!!)).toInt()
    }
}
