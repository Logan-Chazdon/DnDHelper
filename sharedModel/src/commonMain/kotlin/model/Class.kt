package model

import kotlinx.serialization.Serializable


@Serializable
open class Class() : ClassEntity() {
    var isBaseClass: Boolean = false
    var level : Int = 0
    open var subclass: Subclass? = null
    var tookGold: Boolean? = false
    var totalNumOnGoldDie: Int? = 1
    var featsGranted: MutableList<Feat>? = null
    var abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf()
    var levelPath: MutableList<Feature>? = null

    constructor(
        id: Int = 0,
        isBaseClass: Boolean = false,
        level: Int = 1,
        subclass: Subclass? = null,
        tookGold: Boolean? = null,
        totalNumOnGoldDie: Int? = null,
        featsGranted: MutableList<Feat>? = mutableListOf(),
        abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
        name: String,
        hitDie: Int = 8,
        subclassLevel: Int = 0,
        levelPath: MutableList<Feature>? = mutableListOf(),
        proficiencyChoices: List<ProficiencyChoice> = emptyList(),
        proficiencies: List<Proficiency> = emptyList(),
        equipmentChoices: List<ItemChoice> = emptyList(),
        equipment: List<ItemInterface> = emptyList(),
        spellCasting: SpellCasting? = null,
        pactMagic: PactMagic? = null,
        startingGoldD4s: Int = 4,
        startingGoldMultiplier: Int = 10
    ) : this() {
        this.name = name
        this.hitDie = hitDie
        this.subclassLevel = subclassLevel
        this.proficiencyChoices = proficiencyChoices
        this.proficiencies = proficiencies
        this.equipmentChoices = equipmentChoices
        this.equipment = equipment
        this.spellCasting = spellCasting
        this.pactMagic = pactMagic
        this.startingGoldD4s = startingGoldD4s
        this.startingGoldMultiplier = startingGoldMultiplier
        this.id = id

        this.isBaseClass = isBaseClass
        this.level = level
        this.subclass = subclass
        this.tookGold = tookGold
        this.totalNumOnGoldDie = totalNumOnGoldDie
        this.featsGranted = featsGranted
        this.abilityImprovementsGranted = abilityImprovementsGranted
        this.levelPath = levelPath
    }

    constructor(classEntity: ClassEntity, features: MutableList<Feature>) :
            this(
                id = classEntity.id,
                isBaseClass = false,
                level = 1,
                subclass = null,
                tookGold = null,
                totalNumOnGoldDie = null,
                featsGranted = null,
                abilityImprovementsGranted = mutableListOf(),
                name = classEntity.name,
                hitDie = classEntity.hitDie,
                subclassLevel = classEntity.subclassLevel,
                levelPath = features,
                proficiencyChoices = classEntity.proficiencyChoices,
                proficiencies = classEntity.proficiencies,
                equipmentChoices = classEntity.equipmentChoices,
                equipment = classEntity.equipment,
                spellCasting = classEntity.spellCasting,
                pactMagic = classEntity.pactMagic,
                startingGoldD4s = classEntity.startingGoldD4s,
                startingGoldMultiplier = classEntity.startingGoldMultiplier
            )

    fun longRest() {
        levelPath!!.forEach {
            it.recharge(level)
        }
        pactMagic?.pactSlots?.get(level - 1)?.recharge(level)
    }
}
