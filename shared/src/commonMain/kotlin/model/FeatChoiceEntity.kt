package model




open class FeatChoiceEntity(
    val name: String,
    val choose : Int,
) {
    open var id = 0

    fun toFeatChoice(chosen: List<Feat>, from : List<Feat>) : FeatChoice {
        return FeatChoice(
            name = name,
            choose = choose,
            from = from,
            chosen = chosen
        )
    }
}