package gmail.loganchazdon.dndhelper.model

import androidx.room.Embedded


class Class (
    id: Int = 0,
    var isBaseClass: Boolean = false,
    var level: Int = 1,
    @Embedded
    var subclass: Subclass? = null,
    var tookGold: Boolean? = null,
    var totalNumOnGoldDie : Int? = null,
    var featsGranted: MutableList<Feat>? = mutableListOf(),
    var abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    name: String,
    hitDie: Int = 8,
    subClasses: List<Subclass> = emptyList(),
    subclassLevel: Int,
    var levelPath: MutableList<Feature>? = mutableListOf(),
    proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    proficiencies: List<Proficiency> = emptyList(),
    equipmentChoices: List<ItemChoice> = emptyList(),
    equipment: List<ItemInterface> = emptyList(),
    spellCasting : SpellCasting? = null,
    pactMagic: PactMagic? = null,
    startingGoldD4s: Int,
    startingGoldMultiplier : Int = 10
) : ClassEntity(
    name, hitDie, subClasses, subclassLevel, proficiencyChoices, proficiencies, equipmentChoices, equipment, spellCasting, pactMagic, startingGoldD4s, startingGoldMultiplier, id
) {

    fun longRest() {
        levelPath!!.forEach{
            it.recharge(level)
        }
        pactMagic?.pactSlots?.get(level - 1)?.recharge(level)
    }
}
