package com.example.dndhelper.model

import com.example.dndhelper.model.utils.getValueInCopper

class Backpack {
    var backgroundItems = mutableListOf<ItemInterface>()
    var classItems = mutableListOf<ItemInterface>()
    var addedItems = mutableListOf<ItemInterface>()


    val allItems : List<ItemInterface>
        get() {
            val result = mutableListOf<ItemInterface>()
            result.addAll(backgroundItems)
            result.addAll(classItems)
            result.addAll(addedItems)
            return result
        }

    fun deleteItemAtIndex(itemToDeleteIndex: Int) {
        when {
            itemToDeleteIndex < backgroundItems.size -> {
                backgroundItems.removeAt(itemToDeleteIndex)
            }
            itemToDeleteIndex < classItems.size + backgroundItems.size -> {
                classItems.removeAt(itemToDeleteIndex - backgroundItems.size)
            }
            else -> {
                addedItems.removeAt(itemToDeleteIndex - backgroundItems.size - classItems.size)
            }
        }
    }


    var classCurrency = Currency.getEmptyCurrencyMap()
    var backgroundCurrency = Currency.getEmptyCurrencyMap()
    var addedCurrency = Currency.getEmptyCurrencyMap()

    val allCurrency : Map<String, Currency>
    get() {
        val result = mutableMapOf<String, Currency>()
        val keys = classCurrency.keys
        for(key in keys) {
            result[key] = classCurrency[key]!!.copy()
            result[key]!!.amount += backgroundCurrency[key]!!.amount
            result[key]!!.amount += addedCurrency[key]!!.amount
        }
        return result
    }

    //This just tries to remove currency from all the maps until it succeeds.
    //Returns any remainder
    private fun removeCurrencyInCopper(key: String, amount: Int) : Int{
        var amountRemoved = 0
        if(addedCurrency[key]!!.getValueInCopper >= amount - amountRemoved) {
            return addedCurrency[key]!!.subtractInCopper(amount - amountRemoved)
        } else {
            amountRemoved += addedCurrency[key]!!.amount
            addedCurrency[key]!!.amount = 0
        }

        if(backgroundCurrency[key]!!.getValueInCopper >= amount - amountRemoved) {
            return addedCurrency[key]!!.subtractInCopper(amount - amountRemoved)
        } else {
            amountRemoved += backgroundCurrency[key]!!.amount
            backgroundCurrency[key]!!.amount = 0
        }


        if(addedCurrency[key]!!.getValueInCopper >= amount - amountRemoved) {
            return addedCurrency[key]!!.subtractInCopper(amount - amountRemoved)
        } else {
            amountRemoved += addedCurrency[key]!!.amount
            addedCurrency[key]!!.amount = 0
        }
        return 0
    }

    fun addItem(item: ItemInterface) {
        addedItems.add(item)
    }

    fun addClassItems(items: List<ItemInterface?>) {
        for(itemInterface in items) {
            if (itemInterface != null) {
                when (itemInterface.type) {
                    "Currency" -> {
                        itemInterface as Currency
                        classCurrency[itemInterface.abbreviatedName]!!.amount += itemInterface.amount
                    }
                    else -> {
                        classItems.add(itemInterface)
                    }
                }
            }
        }
    }

    fun addBackgroundItems(items: List<ItemInterface?>) {
        for(itemInterface in items) {
            if (itemInterface != null) {
                when (itemInterface.type) {
                    "Currency" -> {
                        itemInterface as Currency
                        backgroundCurrency[itemInterface.abbreviatedName]!!.amount += itemInterface.amount
                    }
                    else -> {
                        backgroundItems.add(itemInterface)
                    }
                }
            }
        }
    }

    //Return true if the character has enough money and then subtract  the value
    //If they do not simply return false
    fun subtractCurrency(cost: Map<String, Currency>): Boolean {
        if(cost.getValueInCopper() <= allCurrency.getValueInCopper()) {
            val totalToBeRemovedInCopper = cost.getValueInCopper()
            var totalRemovedInCopper = 0
            //Loop over the list backwards so that we don't just remove plat
            for(i in allCurrency.keys.reversed()) {
                //If the value is less than what we need to pay remove all
                //and add the amount to the total removed
                if(allCurrency[i]!!.getValueInCopper < totalToBeRemovedInCopper - totalRemovedInCopper) {
                    totalRemovedInCopper += allCurrency[i]!!.getValueInCopper
                    removeCurrencyInCopper(key = i, amount = allCurrency[i]!!.getValueInCopper)
                }
                else { //If the value is greater than what we need to pay. The simply remove the amount remaining.
                    //Add back any change
                    val change = removeCurrencyInCopper(key = i, amount = totalToBeRemovedInCopper - totalRemovedInCopper)
                    addedCurrency["cp"]!!.amount += change
                    return true
                }
            }
            return true
        }
        return false
    }

    fun applyInfusion(targetItem: ItemInterface, infusion: Infusion) {
        val allItemLists = listOf<List<ItemInterface>>(backgroundItems, classItems, addedItems)

        fun attemptInfusion(items: List<ItemInterface?>) : Boolean {
            items.forEachIndexed { index, item ->
                if(item == targetItem) {
                    items[index]?.infusions?.plusAssign(infusion)
                    return true
                }
            }
            return false
        }

        allItemLists.forEach {
            if(attemptInfusion(it))
                return
        }
    }

    fun removeInfusion(infusion: Infusion) {
        val allItemLists = listOf<List<ItemInterface>>(backgroundItems, classItems, addedItems)

        fun attemptDeinfusion(items: List<ItemInterface>): Boolean {
            items.forEachIndexed { index, item ->
                if(item.infusions?.contains(infusion) == true) {
                    items[index].infusions?.remove(infusion)
                    return true
                }
            }
            return false
        }

        allItemLists.forEach {
            if(attemptDeinfusion(it))
                return
        }
    }

    val allWeapons: List<Weapon>
    get() {
        val result = mutableListOf<Weapon>()
        allItems.forEach {
            if(it is Weapon) {
                result.add(it)
            }
        }
        return result
    }
}