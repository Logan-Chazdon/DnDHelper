package com.example.dndhelper.repository.dataClasses

data class Feature(
    val name: String,
    val description: String,
    val level: Int = 1,
    val choiceNum: Int = 0,
    val options: MutableList<Feature>?
) {

}