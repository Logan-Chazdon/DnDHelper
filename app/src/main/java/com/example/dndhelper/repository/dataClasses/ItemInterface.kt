package com.example.dndhelper.repository.dataClasses

interface ItemInterface {
    val type : String
    val name : String?
    val index: String?
    val desc: String?
    val itemRarity : String?
    val cost : Map<String, Currency>?
    val weight : Int?
    val charges: Resource?
}
