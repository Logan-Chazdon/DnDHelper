package model

data class ProficiencyChoice(
    val name: String,
    val choose: Int,
    val from: List<Proficiency>
){
    var chosen: List<Proficiency>
    get() {
        return from.filter {
            chosenByString.contains(it.name)
        }
    }
    set(value : List<Proficiency>) {
        val newChosen = mutableListOf<String>()
        value.forEach {
            newChosen += it.name!!
        }
        chosenByString = newChosen
    }

    var chosenByString: List<String> = emptyList()
}