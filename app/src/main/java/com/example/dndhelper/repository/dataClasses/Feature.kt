package com.example.dndhelper.repository.dataClasses

data class Feature(
    val name: String,
    val description: String,
    val level: Int = 1,
    val choiceNum: Int = 0,
    val options: MutableList<Feature>?
) {
    var chosen : List<Feature>? = null
    var resource: Resource? = null

    fun recharge(basis: Int) {
        resource?.recharge(basis)
        chosen?.forEach {
            it.resource?.recharge(basis)
        }
    }
}