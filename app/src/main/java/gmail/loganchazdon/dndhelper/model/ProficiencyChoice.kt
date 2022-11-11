package gmail.loganchazdon.dndhelper.model

data class ProficiencyChoice(
    val name: String,
    val choose: Int,
    val from: List<Proficiency>
){
    val chosen: List<Proficiency>
    get() {
        return from.filter {
            chosenByString.contains(it.name)
        }
    }

    var chosenByString: List<String> = emptyList()
}