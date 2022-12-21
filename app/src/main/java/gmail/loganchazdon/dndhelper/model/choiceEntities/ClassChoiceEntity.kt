package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity

@Entity(primaryKeys = ["characterId", "classId"])
data class ClassChoiceEntity(
    val characterId : Int,
    val classId : Int,
    val level: Int = 1,
    val isBaseClass : Boolean,
    val totalNumOnGoldDie : Int? = null,
    val abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    val tookGold: Boolean,
    val proficiencyChoices: List<List<String>>,
)
