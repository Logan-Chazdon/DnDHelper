package com.example.dndhelper.repository.dataClasses

data class Invocation(
    val name: String,
    val desc : String,
    val prerequisite: Prerequisite?,
)
