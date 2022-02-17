package com.example.dndhelper.repository.dataClasses

import com.example.dndhelper.repository.dataClasses.utils.getValueInCopper

data class TargetItemFilter(
    val itemTypes: List<String>?,
    val minimumCost: Map<String, Currency>?,
    val properties: List<Property>?
) {
    fun calculate(item: ItemInterface) : Boolean {
        //If the item is not one of the target types
        if(!checkItemTypes(item)) {
            return false
        }

        //If the item doesn't meet the minimum cost requirement return false.
        if(!checkMinimumCost(item)) {
            return false
        }

        //If the item doesn't have all required properties return false.
        if(!checkProperties(item)) {
            return false
        }

        return true
    }

    private fun checkItemTypes(item: ItemInterface) : Boolean {
        //If the target item filter doesn't specify a target item type then just return true.
        if(itemTypes == null) {
            return true
        }
        //Check for the item type in the list of target itemTypes
        itemTypes.forEach {
            if(it == item.type) {
                return true
            }
        }
        return false
    }

    private fun checkMinimumCost(item: ItemInterface) : Boolean {
        //If the filter doesn't specify a cost return true.
        if(minimumCost == null) {
            return true
        }
        //If the item doesn't have a cost return false.
        //Otherwise return whether or not the item costs more than the minimum.
        return (item.cost?.getValueInCopper() ?: 0) < minimumCost.getValueInCopper()
    }

    private fun checkProperties(item: ItemInterface) : Boolean {
        //If the filter doesn't specify properties return true
        if(properties == null) {
            return true
        }
        //Check the item for the required properties.
        properties.forEach {
            //As only weapons have properties we must check if the item is a weapon first.
            if(item is Weapon) {
                //If the item lacks the property return false
                if(item.properties?.contains(it) == false){
                    return false
                }
            } else {
                return false
            }
        }
        //If its passed all checks return true
        return true
    }
}
