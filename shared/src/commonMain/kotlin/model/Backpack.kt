package model

import model.utils.getValueInCopper

class Backpack {
    var equippedArmor: Armor = Armor.none
    var equippedShield: Shield? = null


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
            itemToDeleteIndex < backgroundItems.size ->
                deleteIndexFrom(itemToDeleteIndex, backgroundItems)

            itemToDeleteIndex < classItems.size + backgroundItems.size ->
                deleteIndexFrom(itemToDeleteIndex - backgroundItems.size, classItems)

            else ->
                deleteIndexFrom(itemToDeleteIndex - backgroundItems.size - classItems.size, addedItems)
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
        val currencyGroups = listOf(addedCurrency, backgroundCurrency, classCurrency)

        currencyGroups.forEach {
            if(it[key]!!.getValueInCopper >= amount - amountRemoved) {
                return it[key]!!.subtractInCopper(amount - amountRemoved)
            } else {
                amountRemoved += it[key]!!.amount
                addedCurrency[key]!!.amount = 0
            }
        }

        return 0
    }

    private fun deleteIndexFrom(index: Int, target : MutableList<ItemInterface>) {
        //Make sure to remove the item from any places it being used.
        when(val item = target[index]) {
            is Armor -> {
                if(equippedArmor == item && allItems.count { it == item} == 1) {
                    equippedArmor = Armor.none
                }
            }
            is Shield -> {
                if(equippedShield == item && allItems.count { it == item} == 1) {
                    equippedShield = null
                }
            }
        }
        target.removeAt(index)
    }

    fun getItemByIndex(index: Int) : ItemInterface {
        return when {
            index < backgroundItems.size ->
                backgroundItems[index]
            index < classItems.size + backgroundItems.size ->
                classItems[index - backgroundItems.size]
            else ->
                addedItems[index - backgroundItems.size - classItems.size]
        }
    }

    private fun addItemTo(item: ItemInterface, target: MutableList<ItemInterface>) {
        //Automatically equip the item if we don't have something like it equipped.
        when(item) {
            is Armor -> {
                if(equippedArmor == Armor.none) {
                    equippedArmor = item
                }
            }
            is Shield -> {
                if(equippedShield == null) {
                    equippedShield = item
                }
            }
        }
        target.add(item)
    }

    fun addItem(item: ItemInterface) {
        addItemTo(item, addedItems)
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
                        addItemTo(itemInterface, classItems     )
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
                        addItemTo(itemInterface, backgroundItems)
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

    fun replaceItemAt(item: ItemInterface, index: Int) {
        when {
            index < backgroundItems.size ->
                backgroundItems[index] = item
            index < classItems.size + backgroundItems.size ->
                classItems[index - backgroundItems.size] = item
            else ->
                addedItems[index - backgroundItems.size - classItems.size] = item
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