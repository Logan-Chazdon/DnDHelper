package gmail.loganchazdon.dndhelper.model

class Subclass (
    name: String,
    spells: List<Pair<Int, Spell>>?,
    spellAreFree: Boolean,
    var features: List<Feature>?,
    spellCasting: SpellCasting?
) : SubclassEntity(name, spells, spellAreFree, spellCasting)

