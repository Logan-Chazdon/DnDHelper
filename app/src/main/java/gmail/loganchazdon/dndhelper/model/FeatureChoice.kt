package gmail.loganchazdon.dndhelper.model


class FeatureChoice(
    choose: Choose = Choose(0),
    val options: MutableList<Feature>?,
) : FeatureChoiceEntity(choose){
    var chosen: List<Feature>? = null

    constructor(
        entity: FeatureChoiceEntity,
        chosen: List<Feature>?,
        options: List<Feature>?
    ) : this(choose = entity.choose, options = options?.toMutableList()){
        this.chosen = chosen
    }
}