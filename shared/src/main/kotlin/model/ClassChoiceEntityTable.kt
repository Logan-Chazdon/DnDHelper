package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import model.choiceEntities.ClassChoiceEntity

@Entity(
    primaryKeys = ["characterId", "classId"],
    tableName = "ClassChoiceEntity",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = ClassEntityTable::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
class ClassChoiceEntityTable(
    characterId : Int,
    classId : Int,
    level: Int = 1,
    isBaseClass : Boolean,
    totalNumOnGoldDie : Int? = null,
    abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    tookGold: Boolean,
    proficiencyChoicesByString: List<List<String>>,
) : ClassChoiceEntity(
    characterId,
    classId,
    level,
    isBaseClass,
    totalNumOnGoldDie,
    abilityImprovementsGranted,
    tookGold,
    proficiencyChoicesByString
)


fun ClassChoiceEntity.asTable() : ClassChoiceEntityTable {
    return ClassChoiceEntityTable(
        characterId = characterId,
        classId = classId,
        level = level,
        isBaseClass = isBaseClass,
        totalNumOnGoldDie = totalNumOnGoldDie,
        abilityImprovementsGranted = abilityImprovementsGranted,
        tookGold = tookGold,
        proficiencyChoicesByString = proficiencyChoicesByString
    )
}