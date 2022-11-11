package gmail.loganchazdon.dndhelper.model

data class LanguageChoice(
    var name: String,
    var choose: Int,
    var from: List<Language>
) {
    val chosen: List<Language>
    get() {
        return from.filter {
            chosenByString.contains(it.name)
        }
    }
    var chosenByString = emptyList<String>()
}