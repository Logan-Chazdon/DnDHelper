package model

import kotlinx.serialization.Serializable

@Serializable
class FeatChoice() : FeatChoiceEntity(name = "", choose = 1) {
    lateinit var from: List<Feat>
    var chosen : List<Feat>? = null
    constructor(
        name: String,
        choose : Int,
        from: List<Feat>,
        chosen : List<Feat>? = null
    ) : this() {
        this.name = name
        this.choose = choose
        this.from = from
        this.chosen = chosen
    }

}
