package model

import kotlinx.serialization.Serializable
import model.converters.BooleanAsIntSerializer


@Serializable
open class ClassEntity(
    var name: String = "",
    var hitDie: Int = 8,
    var subclassLevel: Int = 1,
    var proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    var proficiencies: List<Proficiency> = emptyList(),
    var equipmentChoices: List<ItemChoice> = emptyList(),
    var equipment: List<ItemInterface> = emptyList(),
    var spellCasting : SpellCasting? = null,
    var pactMagic: PactMagic? = null,
    var startingGoldD4s: Int = 1,
    var startingGoldMultiplier : Int = 10,
    var id: Int = 0,
    @Serializable(with = BooleanAsIntSerializer::class)
    var isHomebrew : Boolean = false
)