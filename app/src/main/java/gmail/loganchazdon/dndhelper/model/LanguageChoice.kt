package gmail.loganchazdon.dndhelper.model

data class LanguageChoice(
    var name: String,
    var choose: Int,
    var from: List<Language>
) {
    var chosen: List<Language>? = null
}