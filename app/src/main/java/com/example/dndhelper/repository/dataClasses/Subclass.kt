package com.example.dndhelper.repository.dataClasses

data class Subclass (
    var name: String,
    var spells: List<Pair<Int, Spell>>,
    var features: List<Feature>
)

