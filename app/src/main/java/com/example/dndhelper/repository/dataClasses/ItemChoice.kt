package com.example.dndhelper.repository.dataClasses

data class ItemChoice (
    val name : String,
    val choose : Int,
    val from: List<ItemInterface>
) {
    var chosen: List<ItemInterface>? = null
}