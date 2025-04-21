package model.utils

import model.*
import model.repositories.ClassRepository
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

fun Character.addClass(newClass: Class, takeGold: Boolean) {
    //Clear out the unused level path too save memory.
    newClass.levelPath!!.forEach { feature ->
        feature.choices?.forEach {
            it.options?.clear()
        }
    }

    backpack.classCurrency = Currency.getEmptyCurrencyMap()
    backpack.classItems = mutableListOf()
    if (newClass.isBaseClass) {
        if (takeGold) {
            backpack.classCurrency["gp"]?.amount = (backpack.classCurrency["gp"]?.amount ?: 0) +
                    (newClass.totalNumOnGoldDie ?: 0) * newClass.startingGoldMultiplier
        } else {
            newClass.equipment.let { items ->
                for (itemInterface in items) {
                    backpack.addClassItems(listOf(itemInterface))
                }
            }

            newClass.equipmentChoices.forEach {
                it.chosen?.let { items -> backpack.classItems.addAll(items.flatten()) }
            }
        }
    }
    val alreadyHasCasterClass = classes.run {
        var result = false
        for (it in this.values) {
            if((it.spellCasting?.type ?: 0.0) != 0.0) {
                result = true
                break
            }
        }
        result
    }
    classes[newClass.name] = newClass


    spellSlots = if(alreadyHasCasterClass && ((newClass.spellCasting?.type
            ?: 0.0) != 0.0 || newClass.subclass?.spellCasting?.type != 0.0)
    ) {
        //Use the spells slots from the multiclass table
        ClassRepository.getMulticlassSpellSlots(totalCasterLevels)
    } else {
        //Get the spell slots from the class itself
        newClass.spellCasting?.spellSlotsByLevel?.get(newClass.level - 1)
            ?: newClass.subclass?.spellCasting?.spellSlotsByLevel?.get(newClass.level - 1)
            ?: listOf()
    }

    this.longRest()
}

fun Character.getAllSpellSlots(): List<Resource> {
    val slots = mutableListOf<Resource>()
    slots.addAll(spellSlots)
    classes.forEach { (_, clazz) ->
        clazz.pactMagic?.pactSlots?.let {
            val level = allSpellLevels.first { pair -> pair.second == it[clazz.level - 1].name }.first
            val maxAmount = it[clazz.level - 1].maxAmountType.toInt()
            val amount = it[clazz.level - 1].currentAmount
            if(slots.size  >= level) {
                val temp = slots[level - 1].copy()
                temp.currentAmount = temp.currentAmount + amount
                temp.maxAmountType = (maxAmount + temp.maxAmountType.toInt()).toString()
                slots[level - 1] = temp
            } else {
                slots.add(
                    Resource(
                        name = allSpellLevels[level - 1].second,
                        currentAmount = amount,
                        maxAmountType = maxAmount.toString(),
                        rechargeAmountType = maxAmount.toString()
                    )
                )
            }
        }
    }
    return slots
}