package com.example.dndhelper.repository.dataClasses


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.dataClasses.utils.getValueInCopper
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="characters")
@TypeConverters(Converters::class)
data class Character(
    @ColumnInfo(name="name")
    var name: String,
    var race: Race? = null
){
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0

    var baseStats = mutableMapOf<String, Int>()

    var classes = mutableListOf<Class>()

    var background: Background? = null

    var backpack : MutableList<ItemInterface> = mutableListOf()


    var currency = Currency.getEmptyCurrencyList()

    var equiptArmor = Armor.none

    fun getStats(): MutableMap<String, Int> {
        return baseStats  //TODO add bonuses and such
    }

    fun getStat(name: String): Int? {
        return baseStats[name] //TODO add bonuses and such
    }

    fun getStatMod(name: String): Int {
        return (getStat(name)!! - 10) / 2
    }



    //TODO we might be over removing currency without adding any back in conversions
    //Return true if the character has enough money and then subtract  the value
    //If they do not simply return false
    fun subtractCurrency(cost: List<Currency>): Boolean {
        if(cost.getValueInCopper() <= currency.getValueInCopper()) {
            val totalToBeRemovedInCopper = cost.getValueInCopper()
            var totalRemovedInCopper = 0
            //Loop over the list backwards so that we don't just remove plat
            for(i in currency.indices.reversed()) {
                //If the value is less than what we need to pay remove all
                //and add the amount to the total removed
                if(currency[i].getValueInCopper < totalToBeRemovedInCopper - totalRemovedInCopper) {
                    totalRemovedInCopper += currency[i].getValueInCopper
                    currency[i].amount = 0
                }
                else { //If the value is greater than what we need to pay. The simply remove the amount remaining.
                    currency[i].subtractInCopper(totalToBeRemovedInCopper - totalRemovedInCopper)
                    return true
                }
            }
            return true
        }
        return false
    }
}
