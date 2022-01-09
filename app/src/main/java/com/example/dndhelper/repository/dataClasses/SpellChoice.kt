package com.example.dndhelper.repository.dataClasses

data class SpellChoice(
    val choose: Int,
    val from: List<Spell>
) {
    var chosen: List<Spell>? = null
}
