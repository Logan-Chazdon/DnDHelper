package com.example.dndhelper.repository.dataClasses

data class ProficiencyChoice(
    val name: String,
    val desc: String,
    val choose: Int,
    val from: List<Proficiency>
)
