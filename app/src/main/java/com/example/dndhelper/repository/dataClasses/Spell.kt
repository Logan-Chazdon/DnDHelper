package com.example.dndhelper.repository.dataClasses

data class Spell(
    val name: String,
    val level: Int,
    val components: List<String>,
    val itemComponents: List<Item>,
    val school: String,
    val desc: String,
    val range: Int,
    val area: String,
    val castingTime: String,
    val duration: String,
    val classes: List<String>,
    val damage: String
)