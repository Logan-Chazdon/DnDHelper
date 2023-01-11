package gmail.loganchazdon.dndhelper.model

class Subclass(
    name: String,
    spells: List<Pair<Int, Spell>>?,
    spellAreFree: Boolean,
    var features: List<Feature>?,
    spellCasting: SpellCasting?
) : SubclassEntity(name, spells, spellAreFree, spellCasting) {
    constructor(subclassEntity: SubclassEntity, features: List<Feature>?) :
            this(
                name = subclassEntity.name,
                spells = subclassEntity.spells,
                spellAreFree = subclassEntity.spellAreFree,
                features = features,
                spellCasting = subclassEntity.spellCasting
            ) {
                this.subclassId = subclassEntity.subclassId
            }
}

