package gmail.loganchazdon.dndhelper.model


class Class (
    id: Int = 0,
    isBaseClass: Boolean = false,
    level: Int = 1,
    subclass: Subclass? = null,
    tookGold: Boolean? = null,
    totalNumOnGoldDie : Int? = null,
    featsGranted: MutableList<Feat> = mutableListOf(),
    abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
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
    name, hitDie, subClasses, subclassLevel, proficiencyChoices, proficiencies, equipmentChoices, equipment, spellCasting, pactMagic, startingGoldD4s, startingGoldMultiplier, id, isBaseClass, level, subclass, tookGold, totalNumOnGoldDie, featsGranted, abilityImprovementsGranted
) {

    fun longRest() {
        levelPath!!.forEach{
            it.recharge(level)
        }
        pactMagic?.pactSlots?.get(level - 1)?.recharge(level)
    }
}
