package model

class FeatChoice(
    name: String,
    choose : Int,
    val from: List<Feat>,
    var chosen : List<Feat>? = null
) : FeatChoiceEntity(name, choose)
