package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.ClassEntity

@Entity(
    primaryKeys = ["characterId", "classId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = ClassEntity::class,
            childColumns = ["classId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class ClassChoiceEntity(
    val characterId : Int,
    val classId : Int,
    val level: Int = 1,
    val isBaseClass : Boolean,
    val totalNumOnGoldDie : Int? = null,
    val abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    val tookGold: Boolean,
    val proficiencyChoicesByString: List<List<String>>,
)
