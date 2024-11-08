package model

import kotlinx.serialization.Serializable

@Serializable
class FeatureChoice() : FeatureChoiceEntity() {
    var chosen: List<Feature>? = null
    var options: MutableList<Feature>? = null
    constructor(
        choose: Choose = Choose(0),
        options: MutableList<Feature>?,
    ) : this() {
        this.choose = choose
        this.options = options
    }

    constructor(
        entity: FeatureChoiceEntity,
        chosen: List<Feature>?,
        options: List<Feature>?
    ) : this(choose = entity.choose, options = options?.toMutableList()) {
        this.chosen = chosen
        this.id = entity.id
    }
}