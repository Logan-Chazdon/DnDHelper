package gmail.loganchazdon.dndhelper.model

class FeatChoice(
    val name: String,
    val choose : Int,
    val from: List<Feat>,
    var chosen : List<Feat>? = null
)
