package gmail.loganchazdon.dndhelper.model

class Subclass(
    name: String,
    val spells: List<Pair<Int, Spell>>?,
    spellAreFree: Boolean,
    var features: List<Feature>?,
    spellCasting: SpellCasting?
) : SubclassEntity(name, spellAreFree, spellCasting) {
    constructor(subclassEntity: SubclassEntity, features: List<Feature>?, spells: List<Pair<Int, Spell>>? = emptyList()) :
            this(
                name = subclassEntity.name,
                spells =spells,
                spellAreFree = subclassEntity.spellAreFree,
                features = features,
                spellCasting = subclassEntity.spellCasting
            ) {
                this.subclassId = subclassEntity.subclassId
            }
}

