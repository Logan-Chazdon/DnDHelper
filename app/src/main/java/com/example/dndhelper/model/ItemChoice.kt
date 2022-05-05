package com.example.dndhelper.model

data class ItemChoice (
    val name : String,
    val choose : Int,
    val from: List<List<ItemInterface>>,
    var chosen: List<List<ItemInterface>>? = null
)