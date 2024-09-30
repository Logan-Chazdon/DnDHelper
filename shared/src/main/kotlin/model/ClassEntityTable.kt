package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import model.database.converters.Converters


@Entity(tableName = "classes")
@TypeConverters(Converters::class)
class ClassEntityTable(
    name: String,
    hitDie: Int = 8,
    subclassLevel: Int,
    proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    proficiencies: List<Proficiency> = emptyList(),
    equipmentChoices: List<ItemChoice> = emptyList(),
    equipment: List<ItemInterface> = emptyList(),
    spellCasting: SpellCasting? = null,
    pactMagic: PactMagic? = null,
    startingGoldD4s: Int,
    startingGoldMultiplier: Int = 10,
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    isHomebrew: Boolean = false
) : ClassEntity(
    name,
    hitDie,
    subclassLevel,
    proficiencyChoices,
    proficiencies,
    equipmentChoices,
    equipment,
    spellCasting,
    pactMagic,
    startingGoldD4s,
    startingGoldMultiplier,
    id,
    isHomebrew
)

fun ClassEntity.asTable(): ClassEntityTable {
    return ClassEntityTable(
        name,
        hitDie,
        subclassLevel,
        proficiencyChoices,
        proficiencies,
        equipmentChoices,
        equipment,
        spellCasting,
        pactMagic,
        startingGoldD4s,
        startingGoldMultiplier,
        id,
        isHomebrew
    )
}