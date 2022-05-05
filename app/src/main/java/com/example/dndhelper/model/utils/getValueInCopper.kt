package com.example.dndhelper.model.utils
import com.example.dndhelper.model.Currency

//Returns the value of a list of coins as its total value in copper coins
fun Map<String, Currency>.getValueInCopper(): Int {
    var value = 0
    for(currencyType in this) {
        value += currencyType.value.getValueInCopper
    }
    return value
}
