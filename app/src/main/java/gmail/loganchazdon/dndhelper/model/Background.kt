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
) : BackgroundEntity(name, desc, spells, proficiencies, languages, equipment, equipmentChoices) {

    constructor(
        entity: BackgroundEntity,
        features : List<Feature>?,
        languageChoices : List<LanguageChoice>? = null,
        proficiencyChoices : List<ProficiencyChoice>? = null,
    ) : this(
        name = entity.name,
        desc = entity.desc,
        spells = entity.spells,
        proficiencies = entity.proficiencies,
        languages = entity.languages,
        equipment = entity.equipment,
        equipmentChoices = entity.equipmentChoices,
        features = features,
        proficiencyChoices = proficiencyChoices,
        languageChoices = languageChoices
    )
}