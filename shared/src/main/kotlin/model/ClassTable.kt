package model

import androidx.room.Embedded

class ClassTable(
    id: Int = 0,
    isBaseClass: Boolean = false,
    level: Int = 1,
    @Embedded
    override var subclass: Subclass? = null,
    tookGold: Boolean? = null,
    totalNumOnGoldDie: Int? = null,
    featsGranted: MutableList<Feat>? = mutableListOf(),
    abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    name: String,
    hitDie: Int = 8,
    subclassLevel: Int,
    levelPath: MutableList<Feature>? = mutableListOf(),
    proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    proficiencies: List<Proficiency> = emptyList(),
    equipmentChoices: List<ItemChoice> = emptyList(),
    equipment: List<ItemInterface> = emptyList(),
    spellCasting: SpellCasting? = null,
    pactMagic: PactMagic? = null,
    startingGoldD4s: Int,
    startingGoldMultiplier: Int = 10
) : Class(
    id,
    isBaseClass,
    level,
    subclass,
    tookGold,
    totalNumOnGoldDie,
    featsGranted,
    abilityImprovementsGranted,
    name,
    hitDie,
    subclassLevel, levelPath,
    proficiencyChoices,
    proficiencies,
    equipmentChoices,
    equipment,
    spellCasting,
    pactMagic,
    startingGoldD4s,
    startingGoldMultiplier
) {
}