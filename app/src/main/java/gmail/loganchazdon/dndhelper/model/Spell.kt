package gmail.loganchazdon.dndhelper.model

data class Spell(
    val name: String,
    val level: Int,
    val components: List<String>,
    val itemComponents: List<Item>,
    val school: String,
    val desc: String,
    val range: String,
    val area: String,
    val castingTime: String,
    val duration: String,
    val classes: List<String>,
    val damage: String,
    val isRitual: Boolean = false
) {
    val levelName: String
    get() {
        return when(level) {
            0 -> "Cantrip"
            1 -> "1st level"
            2 -> "2nd level"
            3 -> "3rd level"
            else -> "${level}th level"
        }
    }
}