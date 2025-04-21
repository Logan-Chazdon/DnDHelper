package model

import kotlinx.serialization.Serializable

@Serializable
class Subclass()  : SubclassEntity("", false, null) {
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


    var spells: List<Pair<Int, Spell>>? = null
    var features: List<Feature>? = null

    constructor(
        name: String,
        spells: List<Pair<Int, Spell>>?,
        spellAreFree: Boolean,
        features: List<Feature>?,
        spellCasting: SpellCasting?
    ) : this() {
        this.name = name
        this.spells = spells
        this.spellAreFree = spellAreFree
        this.features = features
        this.spellCasting = spellCasting
    }
}

