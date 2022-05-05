package com.example.dndhelper.model

data class Invocation(
    val name: String,
    val desc : String,
    val prerequisite: Prerequisite?,
)
