package gmail.loganchazdon.dndhelper.model.utils
import gmail.loganchazdon.dndhelper.model.Currency

//Returns the value of a list of coins as its total value in copper coins
fun Map<String, Currency>.getValueInCopper(): Int {
    var value = 0
    for(currencyType in this) {
        value += currencyType.value.getValueInCopper
    }
    return value
}
