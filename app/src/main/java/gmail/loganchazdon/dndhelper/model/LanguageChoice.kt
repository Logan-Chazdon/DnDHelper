package gmail.loganchazdon.dndhelper.model

data class LanguageChoice(
    var name: String,
    var choose: Int,
    var from: List<Language>
) {
    var chosen: List<Language>
    get() {
        return from.filter {
            chosenByString.contains(it.name)
        }
    }
    set(value) {
        val temp = mutableListOf<String>()
        value.forEach {
            it.name?.let { name -> temp.add(name) }
        }
        chosenByString = temp
    }
    var chosenByString = emptyList<String>()
}