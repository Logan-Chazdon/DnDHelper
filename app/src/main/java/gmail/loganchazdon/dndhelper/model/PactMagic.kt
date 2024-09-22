package gmail.loganchazdon.dndhelper.model

data class PactMagic(
    val castingAbility: String,
    val spellsKnown: List<Int>,
    var known: MutableList<Spell> = mutableListOf(),
    val cantripsKnown: List<Int>,
    val pactSlots : List<Resource>
)
