package com.example.dndhelper.repository.dataClasses

import com.google.gson.annotations.SerializedName

data class Currency(
    val amount: Int,
    override val name: String?,
    override val weight: Int?
) : ItemInterface {
   override val type = "Currency"

    override val index: String? = null
    override val desc: String? = null
    override val itemRarity: String? = null
    override val cost: List<Currency>? = null
}