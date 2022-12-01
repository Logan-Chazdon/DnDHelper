package gmail.loganchazdon.dndhelper.model

class Background (
    name: String,
    desc: String,
    spells: List<Spell>?,
    proficiencies : List<Proficiency>,
    val proficiencyChoices : List<ProficiencyChoice>? = null,
    var features : List<Feature>? = null,
    languages : List<Language>,
    val languageChoices : List<LanguageChoice>? = null,
    equipment : List<ItemInterface>,
    equipmentChoices: List<ItemChoice>
) : BackgroundEntity(name, desc, spells, proficiencies, languages, equipment, equipmentChoices)