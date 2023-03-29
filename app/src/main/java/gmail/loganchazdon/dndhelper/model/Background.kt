package gmail.loganchazdon.dndhelper.model

class Background (
    name: String,
    desc: String,
    spells: List<Spell>?,
    proficiencies : List<Proficiency>,
    proficiencyChoices : List<ProficiencyChoice>? = null,
    var features : List<Feature>? = null,
    languages : List<Language>,
    languageChoices : List<LanguageChoice>? = null,
    equipment : List<ItemInterface>,
    equipmentChoices: List<ItemChoice>
) : BackgroundEntity(name, desc, spells, proficiencies, proficiencyChoices, languages, languageChoices, equipment, equipmentChoices) {

    constructor(
        entity: BackgroundEntity,
        features : List<Feature>?,
    ) : this(
        name = entity.name,
        desc = entity.desc,
        spells = entity.spells,
        proficiencies = entity.proficiencies,
        languages = entity.languages,
        equipment = entity.equipment,
        equipmentChoices = entity.equipmentChoices,
        features = features,
        proficiencyChoices = entity.proficiencyChoices,
        languageChoices = entity.languageChoices
    ) {
        this.id = entity.id
    }
}