package gmail.loganchazdon.dndhelper.model


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlin.math.floor

@Entity(tableName="characters")
@TypeConverters(Converters::class)
data class Character(
    @ColumnInfo(name="name")
    var name: String,
    var personalityTraits: String = "",
    var ideals: String = "",
    var bonds: String = "",
    var flaws: String = "",
    var notes: String = "",
    var race: Race? = null,
    var currentHp: Int = 0,
    var tempHp: Int = 0,
    var conditions: MutableList<String> = mutableListOf<String>(),
    var resistances: MutableList<String> = mutableListOf<String>(),
    var classes: MutableMap<String, Class> = mutableMapOf(),
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0,
    var statGenerationMethodIndex: Int = 0,
    var baseStats: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var background: Background? = null,
    var backpack: Backpack = Backpack(),
    var inspiration: Boolean = false,
    var positiveDeathSaves: Int = 0,
    var negativeDeathSaves: Int = 0,
    var spellSlots: List<Resource> = listOf(),
    val addedLanguages: MutableList<Language> = mutableListOf<Language>(),
    val addedProficiencies: MutableList<Proficiency> = mutableListOf<Proficiency>()
){
    val hasSpells: Boolean
    get() {
        classes.forEach { (_, clazz) ->
            clazz.pactMagic?.let {
                return true
            }
            if(clazz.spellCasting?.type != 0.0) {
                return true
            }
        }

        if(!additionalSpells.isNullOrEmpty()) {
            return true
        }

        return false
    }

    val feats: List<Feat>
    get() {
        val result = mutableListOf<Feat>()
        //Race
        race?.allFeats?.let { result.addAll(it) }

        //Classes
        classes.forEach { (_, clazz) ->
            result.addAll(clazz.featsGranted)
        }
        return result
    }

    val abilityScoreIncreases: Map<String, Int>
    get() {
        val result: MutableMap<String, Int> = mutableMapOf(
            "Str" to 0,
            "Dex" to 0,
            "Con" to 0,
            "Int" to 0,
            "Wis" to 0,
            "Cha" to 0
        )

        classes.forEach { (_, clazz) ->
            clazz.abilityImprovementsGranted.forEach {
                it.forEach { item ->
                    result[item.key] = result[item.key]?.plus(item.value) ?: 0
                }
            }
        }

        return result
    }

    val hasPactMagic: Boolean
    get() {
        classes.forEach {
            if(it.value.pactMagic != null) {
                return true
            }
        }
        return false
    }

    val groundSpeed: Int
    get() {
        var result = race?.totalGroundSpeed ?: 30
        if(backpack.equippedArmor.strengthPrerequisite ?: 0 > realStats["Str"] ?: 0) {
            result -= 10
        }
        return result
    }
    val armorClass: Int
    get() {
        //TODO implement all other sources of ac.
        var result = backpack.equippedArmor.getAC(getStatMod("Dex"))
        //Check for a feature with a higher ac than the armor.
        //If one is found replace the current ac.
        if(backpack.equippedArmor == Armor.none) {
            features.forEach {
                it.second.ac?.let { armorClass: ArmorClass ->
                    armorClass.calculate(
                        dex = getStatMod("Dex"),
                        con = getStatMod("Con"),
                        wis = getStatMod("Wis")
                    ).let { value ->
                        if (value > result) {
                            result = value
                        }

                    }
                }
            }
        } else {
            //The character is wearing armor so apply effects accordingly.
            features.forEach { feature ->
                feature.second.armorContingentAcBonus?.let {
                    result += it
                }
                feature.second.allChosen.forEach { chosen ->
                    chosen.armorContingentAcBonus?.let {
                        result += it
                    }
                }
            }
        }

        backpack.equippedShield?.let {
            result += it.totalAcBonus
        }
        features.forEach {
            it.second.acBonus?.let { acBonus -> result += acBonus}
        }
        return result
    }

    //All features excluding those granted by feats.
    val displayFeatures : List<Pair<Int, Feature>>
    get() {
        val result = mutableListOf<Pair<Int, Feature>>()
        race?.let { race ->
            race.getAllTraits().forEach {
                result.add(totalClassLevels to it)
            }
        }

        classes.values.forEach {
            it.levelPath.forEach { feature ->
                if(feature.grantedAtLevel <= it.level) {
                    result.add(it.level to feature)
                }
            }

            it.subclass?.features?.forEach { feature ->
                if(feature.grantedAtLevel <= it.level) {
                    result.add(it.level to feature)
                }
            }
        }


        background?.let { background ->
            background.features.forEach {
                result.add(totalClassLevels to it)
            }
        }

        return result
    }


    //Returns a list of integers representing level to features
    val features: List<Pair<Int, Feature>>
    get() {
        val result = mutableListOf<Pair<Int, Feature>>()
        result.addAll(displayFeatures)

        feats.forEach { feat ->
            feat.features?.let { features ->
                features.forEach {
                    result.add(totalClassLevels to it)
                }
            }
        }

        return result
    }

    private val realStats : MutableMap<String, Int>
    get() {
        val stats = baseStats.toMutableMap()
        //Ability Score Increases
        for(item in abilityScoreIncreases.entries) {
            stats[item.key] = stats[item.key]?.plus(item.value) ?: 0
        }

        //Feats
        for(item in feats) {
            if(item.abilityBonuses != null) {
                item.abilityBonuses.forEach {
                    stats[it.ability.substring(0, 3)] =
                        stats[it.ability.substring(0, 3)]?.plus(it.bonus) ?: it.bonus
                }
            }

            if(item.abilityBonusChoice != null) {
                item.abilityBonusChoice.chosen?.forEach {
                    stats[it.ability.substring(0, 3)] =
                        stats[it.ability.substring(0, 3)]?.plus(it.bonus)  ?: it.bonus
                }
            }
        }

        //Races
        race?.getAllAbilityScoreBonuses()?.forEach {
            stats[it.ability.substring(0, 3)] = stats[it.ability.substring(0, 3)]?.plus(it.bonus) ?: 0
        }

        //Ensure Feats Races and Ability Score Increases don't push stats above 20.
        stats.forEach { (name, stat) ->
            if(stat > 20) {
                stats[name] = 20
            }
        }

        return stats
    }

    val maxHp: Int
    get() {
        var newMax = 0
        for(item in classes.values) {
            newMax += if(item.isBaseClass) {
                ((((item.hitDie / 2) + 1) + getStatMod("Con")) * (item.level -1)) + 8 + getStatMod("Con")
            } else {
                (((item.hitDie / 2) + 1) + getStatMod("Con")) * item.level
            }
        }
        features.forEach { feature ->
            feature.second.hpBonusPerLevel?.let {
                newMax += (
                        it * ((feature.first - feature.second.grantedAtLevel) + 1))
            }
        }
        return newMax
    }

    val totalClassLevels: Int
    get() {
        var result = 0
        for(item in classes.values) {
            result = item.level
        }
        return result
    }

    fun addClass(newClass: Class, takeGold: Boolean) {
        //Clear out the unused level path too save memory.
        newClass.levelPath.forEach { feature ->
            feature.choices?.forEach {
                it.options?.clear()
            }
        }

        backpack.classCurrency = Currency.getEmptyCurrencyMap()
        backpack.classItems = mutableListOf()
        if (newClass.isBaseClass) {
            if (takeGold) {
                backpack.classCurrency["gp"]?.amount = (backpack.classCurrency["gp"]?.amount ?: 0) +
                        (newClass.totalNumOnGoldDie ?: 0) * newClass.startingGoldMultiplier
            } else {
                newClass.equipment.let { items ->
                    for (itemInterface in items) {
                        backpack.addClassItems(listOf(itemInterface))
                    }
                }

                newClass.equipmentChoices.forEach {
                    it.chosen?.let { items -> backpack.classItems.addAll(items.flatten()) }
                }
            }
        }
        val alreadyHasCasterClass = classes.run {
            var result = false
            for (it in this.values) {
                if(it.spellCasting?.type ?: 0.0 != 0.0) {
                    result = true
                    break
                }
            }
            result
        }
        classes[newClass.name] = newClass


        if(alreadyHasCasterClass && (newClass.spellCasting?.type ?: 0.0 != 0.0  || newClass.subclass?.spellCasting?.type != 0.0)) {
            //Use the spells slots from the multiclass table
            spellSlots = when(totalCasterLevels) {
                1 -> {
                    listOf(Resource(name = "1st", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"))
                }
                2 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3")
                    )
                }
                3 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2")
                    )
                }
                4 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3")
                    )
                }
                5 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2")
                    )
                }
                6 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3")
                    )
                }
                7 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                8 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2")
                    )
                }
                9 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                10 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2")
                    )
                }
                11 or 12 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                13 or 14-> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "7th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                15 or 16 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "7th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "8th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                    )
                }
                17 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "7th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "8th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "9th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                18 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "7th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "8th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "9th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                19 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "7th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "8th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "9th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                20 -> {
                    listOf(
                        Resource(name = "1st", currentAmount = 4, rechargeAmountType = "4", maxAmountType = "4"),
                        Resource(name = "2nd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "3rd", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "4th", currentAmount = 3, rechargeAmountType = "3", maxAmountType = "3"),
                        Resource(name = "5th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "6th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "7th", currentAmount = 2, rechargeAmountType = "2", maxAmountType = "2"),
                        Resource(name = "8th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1"),
                        Resource(name = "9th", currentAmount = 1, rechargeAmountType = "1", maxAmountType = "1")
                    )
                }
                else -> {
                    listOf()
                }
            }
        } else {
            //Get the spell slots from the class itself
            spellSlots =
                newClass.spellCasting?.spellSlotsByLevel?.get(newClass.level - 1)
                    ?: newClass.subclass?.spellCasting?.spellSlotsByLevel?.get(newClass.level - 1)
                            ?: listOf()
        }

        this.longRest()
    }



    fun setNewBackground(newBackGround: Background) {
        background = newBackGround
        backpack.backgroundCurrency = Currency.getEmptyCurrencyMap()
        backpack.backgroundItems = mutableListOf<ItemInterface>()
        background?.equipment?.let { items ->
            backpack.addBackgroundItems(items)
        }
        background?.equipmentChoices?.forEach {
            it.chosen?.let { chosen ->
                for (item in chosen) {
                    if(it.chosen != null) {
                        backpack.addBackgroundItems(it.chosen!!.flatten())
                    }
                }
            }
        }
    }


    fun getStats(): MutableMap<String, Int> {
        return realStats
    }

    fun getStat(name: String): Int? {
        return realStats[name]
    }

    fun getStatMod(name: String): Int {
        return (getStat(name)!! - 10) / 2
    }

    fun getFormattedClasses(): String {
        var result = ""
        for((i, item) in classes.values.withIndex()) {
            result += "${item.name} ${item.level}"
            if(i != classes.size - 1) {
                result += ", "
            }
        }
        return result
    }

    fun longRest() {
        //Classes
        classes.values.forEach {
            it.longRest()
        }

        //Spell slots
        spellSlots.forEach {
            it.recharge(0)
        }

        //Race
        race?.longRest()

        //Items

        //Hp
        tempHp = 0
        currentHp = maxHp
    }

    private fun checkForExpertise(it: String) : Boolean {
        features.forEach { feature ->
            if(feature.second.name == "Expertise") {
                feature.second.allChosen.forEach { item ->
                    if (item.name == it) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkForProficiencyOrExpertise(it: String) : Int {
        proficiencies.forEach { prof ->
            if(prof.name?.lowercase() ?: "" == it.lowercase()) {
                if(checkForExpertise(prof.name.toString())) {
                    return 2
                }
                return 1
            }
        }
        return 0
    }

    fun checkForProficienciesOrExpertise(stats: List<String>): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        stats.forEach {
            result[it] = checkForProficiencyOrExpertise(it)
        }
        return result
    }

    val totalCasterLevels : Int
    get() {
        var result = 0
        classes.values.forEach{
            result += floor(it.level.toDouble() * (it.spellCasting?.type ?: 0.0)).toInt()
            result += floor(it.level.toDouble() * (it.subclass?.spellCasting?.type ?: 0.0)).toInt()
        }
        return result
    }

    val hasBaseClass: Boolean
    get() {
        classes.forEach {
            if(it.value.isBaseClass) {
                return true
            }
        }
        return false
    }

    val proficiencyBonus : Int
    get() {
        return when(totalClassLevels) {
            in 1..4 -> 2
            in 5..8 -> 3
            in 9..12 -> 4
            in 13..16  -> 5
            in 17..20 -> 6
            else -> 0
        }
    }

    val passives : Map<String, Int>
        get() {
            return mapOf(
                "Passive Perception" to (10 + getStatMod("Wis") + proficiencyBonus),
                "Passive Investigation" to (10 + getStatMod("Int") + proficiencyBonus),
                "Passive Insight" to (10 + getStatMod("Wis") + proficiencyBonus)
            )
        }


    val languages: List<Language>
    get() {
        val result = mutableListOf<Language>()
        race?.getAllLanguages()?.let { result.addAll(it) }
        background?.languages?.let {result.addAll(it) }
        background?.languageChoices?.forEach { choice ->
            choice.chosen?.let { result.addAll(it) }
        }
        features.forEach { feature ->
            feature.second.languages?.let {
                result.addAll(it)
            }
        }
        result.addAll(addedLanguages)
        return result
    }

    val proficiencies: List<Proficiency>
    get() {
        val result = mutableListOf<Proficiency>()
        background?.proficiencies?.let { result.addAll(it) }
        classes.forEach {
            result.addAll(it.value.proficiencies)
            it.value.proficiencyChoices.forEach { choice ->
                choice.chosen?.let { chosen -> result.addAll(chosen) }
            }
        }
        race?.getAllProficiencies()?.let { result.addAll(it) }
        features.forEach { feature ->
            feature.second.proficiencies?.let {
                result.addAll(it)
            }
        }
        result.addAll(addedProficiencies)
        return result
    }

    val isCaster : Boolean
    get() {
        classes.values.forEach {
            if(it.spellCasting?.type ?: 0.0 != 0.0) {
                return true
            }
            if(it.pactMagic != null) {
                return true
            }
        }
        return false
    }

    //Gets spells granted by features, feats etc.
    val additionalSpells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>>
    get() {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        features.forEach {
            it.second.getSpellsGiven().forEach { spell ->
                if(!spells.containsKey(spell.level)) {
                    spells[spell.level] = mutableListOf()
                }
                spells[spell.level]?.add(Pair(first = null, second = spell))
            }
        }

        feats.forEach { feat ->
            feat.features?.forEach {
                it.spells?.forEach { spell ->
                    if(!spells.containsKey(spell.level)) {
                        spells[spell.level] = mutableListOf()
                    }
                    spells[spell.level]?.add(Pair(first = null, second = spell))
                }
            }
        }

        classes.forEach { (_, clazz) ->
            if(clazz.subclass?.spellAreFree == true) {
                val prepared = clazz.spellCasting?.let {
                    if (it.prepareFrom != null) {
                        true
                    } else {
                        null
                    }
                }
                clazz.subclass?.spells?.forEach { (_, spell) ->
                    if(!spells.containsKey(spell.level)) {
                        spells[spell.level] = mutableListOf()
                    }
                    spells[spell.level]?.add(Pair(first = prepared, second = spell))
                }

            }
        }

        background?.spells?.let {
            it.forEach { spell ->
                if(!spells.containsKey(spell.level)) {
                    spells[spell.level] = mutableListOf()
                }
                spells[spell.level]?.add(Pair(first = null, second = spell))
            }
        }

        return spells
    }

    val combatFeatures: List<Feature>
    get() {
        val result = mutableListOf<Feature>()
        features.forEach {
            if(it.second.resource != null) {
                result.add(it.second)
            }
        }
        return result
    }

    fun calculateWeaponAttackBonus(weapon: Weapon) : Int {
        val proficiencyName = weapon.proficiency

        val bonusForProficiency = proficiencies.run {
            var result = 0
            for (item in this) {
                if(item.name == proficiencyName || item.index?.lowercase() == weapon.name?.lowercase()) {
                  result = proficiencyBonus
                  break
                }
            }
            result
        }

        var bonusFromStats = 0
        //All stats the could be used with the weapon.
        val statOptions = mutableListOf<String>()

        //If the weapon is ranged add dex as an option.
        if(weapon.range != "5 ft") {
            statOptions.add("Dex")
        } //If the weapon is melee add Str as an option.
        else {
            statOptions.add("Str")
        }

        //if the weapon has finesse add dex as an option.
        if(weapon.properties?.any { it -> it.name == "Finesse" } == true) {
            statOptions.add("Dex")
        }


        //Use the stat with the highest value.
        statOptions.forEach { statName ->
            getStatMod(statName).let { stat ->
                if(stat > bonusFromStats) {
                        bonusFromStats = stat
                }
            }
        }

        val bonusFromInfusions = weapon.infusions.let { infusions ->
            var total = 0
            infusions?.forEach { infusion ->
                infusion.currentAtkDmgBonus?.let {
                    total += it
                }
            }
            total
        }

        var bonusFromFeatures = 0
        val applyFeature = fun(it : Feature) {
            if(it.rangedAttackBonus != null && weapon.range != "5 ft") {
                bonusFromFeatures += it.rangedAttackBonus
            }
        }
        features.forEach {
            applyFeature(it.second)
            it.second.allChosen.forEach { subfeature ->
                applyFeature(subfeature)
            }
        }

        //TODO add support for magic items.
        return bonusForProficiency + bonusFromStats + bonusFromInfusions + bonusFromFeatures
    }

    fun getAllSpellSlots(): List<Resource> {
        val slots = mutableListOf<Resource>()
        slots.addAll(spellSlots)
        classes.forEach { (_, clazz) ->
            clazz.pactMagic?.pactSlots?.let {
                val level = it[clazz.level - 1].name.toInt()
                val maxAmount = it[clazz.level - 1].maxAmountType.toInt()
                val amount = it[clazz.level - 1].currentAmount
                if(slots.size  == level) {
                    slots[level - 1].currentAmount = slots[level- 1].currentAmount + amount
                    slots[level - 1].maxAmountType = maxAmount.toString()
                    slots[level - 1].rechargeAmountType = maxAmount.toString()
                } else {
                    slots.add(
                        Resource(
                            name = Repository.allSpellLevels[level].second,
                            currentAmount = amount,
                            maxAmountType = maxAmount.toString(),
                            rechargeAmountType = maxAmount.toString()
                        )
                    )
                }
            }
        }
        return slots
    }

    fun totalClassLevelsExcluding(className: String): Int {
        var result = 0
        classes.forEach {
            if(it.key != className) {
                result += it.value.level
            }
        }
        return 0
    }

    val maxHitDice: String
    get() {
        var result = ""
        classes.forEach { _, clazz ->
            result += "${clazz.level}d${clazz.hitDie}"
        }
        return result
    }
}
