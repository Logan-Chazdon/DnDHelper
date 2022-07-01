package gmail.loganchazdon.dndhelper.model

data class SpellChoice(
    val choose: Int,
    val from: List<Spell>
) {
    var chosen: List<Spell>? = null
}
