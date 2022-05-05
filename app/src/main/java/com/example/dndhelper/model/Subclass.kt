package com.example.dndhelper.model

data class Subclass (
    var name: String,
    var spells: List<Pair<Int, Spell>>?,
    val spellAreFree: Boolean,
    var features: List<Feature>,
    var spellCasting: SpellCasting?
)

