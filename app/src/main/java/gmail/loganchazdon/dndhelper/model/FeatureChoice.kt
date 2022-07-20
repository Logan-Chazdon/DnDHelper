package gmail.loganchazdon.dndhelper.model

class FeatureChoice(
    val choose: Choose = Choose(0),
    val options: MutableList<Feature>?,
) {
    var chosen: List<Feature>? = null
}