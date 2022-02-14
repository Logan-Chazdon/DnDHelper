package com.example.dndhelper.repository.localDataSources

import com.example.dndhelper.repository.dataClasses.Resource

data class Infusion(
    val name: String,
    val desc: String,
    val type: String,
    val charges: Resource? = null,
    val acBonus: Int? = null,
    val atkDmgBonus: Int? = null,
    var active: Boolean = false,
    val attuned: Boolean
    )
