package com.example.dndhelper.repository.dataClasses


data class SchoolRestriction(
    val amount : Int,
    val schools: List<String>
) {
    fun isMet(spells: List<Spell>) : Boolean {
        var amountMeeting = 0
        spells.forEach {
            if(schools.contains(it.school)) {
                amountMeeting++
            }
        }
        return amountMeeting >= amount
    }
}