package model.choiceEntities

open class ClassChoiceEntity(
    val characterId : Int,
    val classId : Int,
    val level: Int = 1,
    val isBaseClass : Boolean,
    val totalNumOnGoldDie : Int? = null,
    val abilityImprovementsGranted: MutableList<Map<String, Int>> = mutableListOf(),
    val tookGold: Boolean,
    val proficiencyChoicesByString: List<List<String>>,
)