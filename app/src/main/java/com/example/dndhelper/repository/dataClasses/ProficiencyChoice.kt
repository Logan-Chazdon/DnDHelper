package com.example.dndhelper.repository.dataClasses

data class ProficiencyChoice(
    val name: String,
    val choose: Int,
    val from: List<Proficiency>
){
    var chosen: List<Proficiency>? = null
}