package com.example.dndhelper.repository.dataClasses.utils
import com.example.dndhelper.repository.dataClasses.Currency

//Returns the value of a list of coins as its total value in copper coins
fun List<Currency>.getValueInCopper(): Int {
    var value = 0
    for(currencyType in this) {
        value += currencyType.getValueInCopper
    }
    return value
}