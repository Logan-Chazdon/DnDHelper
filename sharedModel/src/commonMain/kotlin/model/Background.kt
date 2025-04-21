package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Background() : BackgroundEntity() {
    @Transient
    var features: List<Feature>? = null

    constructor(
        name: String,
        desc: String,
        spells: List<Spell>?,
        proficiencies: List<Proficiency>,
        proficiencyChoices: List<ProficiencyChoice>? = null,
        features: List<Feature>? = null,
        languages: List<Language>,
        languageChoices: List<LanguageChoice>? = null,
        equipment: List<ItemInterface>,
        equipmentChoices: List<ItemChoice>
    ) : this() {
        this.name = name
        this.desc = desc
        this.spells = spells
        this.proficiencies = proficiencies
        this.proficiencyChoices = proficiencyChoices
        this.languages = languages
        this.languageChoices = languageChoices
        this.equipment = equipment
        this.equipmentChoices = equipmentChoices
        this.features = features
    }

    constructor(
        entity: BackgroundEntity,
        features: List<Feature>?,
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