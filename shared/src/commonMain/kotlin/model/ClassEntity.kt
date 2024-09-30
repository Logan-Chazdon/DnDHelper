package model



open class ClassEntity(
    open var name: String,
    open var hitDie: Int = 8,
    open var subclassLevel: Int,
    open var proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    open var proficiencies: List<Proficiency> = emptyList(),
    open var equipmentChoices: List<ItemChoice> = emptyList(),
    open var equipment: List<ItemInterface> = emptyList(),
    open val spellCasting : SpellCasting? = null,
    open val pactMagic: PactMagic? = null,
    open val startingGoldD4s: Int,
    open val startingGoldMultiplier : Int = 10,
    open var id: Int = 0,
    var isHomebrew : Boolean = false
)