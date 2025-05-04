package gmail.loganchazdon.dndhelper.model.dataSources

import LogManager
import SharedDataSource
import StringFileResolver
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dataManagers.*
import gmail.loganchazdon.database.*
import gmail.loganchazdon.database.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.database.gsonInstance
import gmail.loganchazdon.dndhelper.model.database.jsonListAdapter
import gmail.loganchazdon.dndhelper.model.database.jsonObjectAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.*
import java.io.File


/**
 * Due to an issue with how sqlite processes composite primary keys we need to remove and then replace
 * instead of update for all database insertions in this class.
 */
@OptIn(DelicateCoroutinesApi::class)
class ServerDataSource(db: Database) {
    val maneuvers = mutableListOf<Feature>()
    val invocations = mutableListOf<Feature>()
    val infusions = mutableListOf<Infusion>()
    val metamagics = mutableListOf<Metamagic>()
    lateinit var abilitiesToSkills: Map<String, List<String>>

    var martialWeapons: List<Weapon> = emptyList()
    var simpleWeapons: List<Weapon> = emptyList()
    var miscItems: List<ItemInterface> = emptyList()
    var armors: List<Armor> = emptyList()
    val items: MutableList<ItemInterface> = mutableListOf()

    val languages = mutableListOf<Language>()

    private fun Any.toJsonList(): JsonArray {
        return jsonListAdapter.decode(gsonInstance.toJson(this))
    }

    private fun Any.toJsonObject(): JsonObject {
        return jsonObjectAdapter.decode(gsonInstance.toJson(this))
    }

    private fun Any?.serialize() : String? {
        if(this == null) {
            return null
        }
        return gsonInstance.toJson(this)
    }

    init {
        val dataSource = SharedDataSource(
            res = StringFileResolver(
                getString = { path ->
                    File("shared/src/androidMain/res/raw/$path").readText()
                }
            ),
            featureManager = FeatureManager(
                insertFeature = {
                    db.featuresQueries.delete(
                        featureId = it.featureId.toLong(),
                        owner = null
                    )
                    db.featuresQueries.insert(
                        Features(
                            featureId = it.featureId.toLong(),
                            name = it.name,
                            description = it.description,
                            index = it.index,
                            grantedAtLevel = it.grantedAtLevel.toLong(),
                            maxTimesChosen = it.maxTimesChosen?.toLong(),
                            prerequisite = it.prerequisite?.toJsonObject(),
                            activationRequirement = it.activationRequirement.toJsonObject(),
                            speedBoost = it.speedBoost?.toJsonObject(),
                            spells = it.spells.serialize(),
                            infusion = it.infusion?.toJsonObject(),
                            maxActive = it.maxActive.toJsonObject(),
                            hpBonusPerLevel = it.hpBonusPerLevel?.toLong(),
                            armorContingentAcBonus = it.armorContingentAcBonus?.toLong(),
                            acBonus = it.acBonus?.toLong(),
                            ac = it.ac?.toJsonObject(),
                            proficiencies = it.proficiencies?.toJsonList(),
                            expertises = it.expertises?.toJsonList(),
                            languages = it.languages?.toJsonList(),
                            extraAttackAndDamageRollStat = it.extraAttackAndDamageRollStat,
                            rangedAttackBonus = it.rangedAttackBonus?.toLong(),
                            owner = null
                        )
                    )
                },
                _insertFeatureChoiceIndexCrossRef = { choiceId, index, levels, classes, schools ->
                    db.featureChoiceIndexCrossRefQueries.delete(
                        choiceId = choiceId.toLong(),
                        owner = null,
                        index = index
                    )
                    db.featureChoiceIndexCrossRefQueries.insert(
                        choiceId = choiceId.toLong(),
                        index = index,
                        levels = gsonInstance.toJson(levels),
                        classes = gsonInstance.toJson(classes),
                        schools = gsonInstance.toJson(schools),
                        owner = null
                    )
                },
                insertFeatureOptionsCrossRef = { featureId, choiceId ->
                    db.featureOptionsCrossRefQueries.delete(
                        owner = null,
                        id = choiceId.toLong(),
                        featureId = featureId.toLong()
                    )
                    db.featureOptionsCrossRefQueries.insert(
                        featureId = featureId.toLong(),
                        id = choiceId.toLong(),
                        owner = null
                    )
                },
                insertFeatureChoice = {
                    db.featureChoiceEntityQueries.delete(
                        owner = null,
                        id = it.id.toLong()
                    )
                    db.featureChoiceEntityQueries.insert(
                        FeatureChoiceEntity(
                            id = it.id.toLong(),
                            choose = it.choose.toJsonObject(),
                            owner = null
                        )
                    )
                },
                insertIndexRef = { ids, index ->
                    db.indexRefQueries.delete(index, null)
                    db.indexRefQueries.insert(
                        index = index,
                        ids = ids.serialize() ?: "",
                        owner = null
                    )
                },
                postManeuvers = {
                    maneuvers.addAll(it)
                },
                postInvocations = {
                    invocations.addAll(it)
                },
                postInfusions = {
                    infusions.addAll(it)
                },
                insertFeatureSpellCrossRef = { spellId, featureId ->
                    db.featureSpellCrossRefQueries.delete(
                        spellId = spellId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                    db.featureSpellCrossRefQueries.insert(
                        spellId = spellId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                },
                insertOptionsFeatureCrossRef = { featureId, choiceId ->
                    db.optionsFeatureCrossRefQueries.delete(
                        featureId = featureId.toLong(),
                        owner = null,
                        choiceId = choiceId.toLong()
                    )
                    db.optionsFeatureCrossRefQueries.insertOptionsFeatureCrossRef(
                        featureId = featureId.toLong(),
                        choiceId = choiceId.toLong(),
                        owner = null
                    )
                },
                getFeatureIdByName = {
                    db.featuresQueries.selectIdByName(it).executeAsOne().toInt()
                }
            ),
            metaMagicManager = MetaMagicManager(
                postAll = {
                    metamagics.addAll(it)
                }
            ),
            featManager = FeatManager(
                insertFeat = {
                    db.featsQueries.delete(
                        owner = null,
                        id = it.id.toLong()
                    )
                    db.featsQueries.insert(
                        id = it.id.toLong(),
                        name = it.name,
                        desc = it.desc,
                        prerequisite = if(it.prerequisite == null) null else it.prerequisite?.toJsonObject(),
                        abilityBonuses = if(it.abilityBonuses == null) null else it.abilityBonuses?.toJsonList(),
                        abilityBonusChoice = if(it.abilityBonusChoice == null) null else it.abilityBonusChoice?.toJsonObject(),
                        owner = null
                    )
                },
                insertFeatFeatureCrossRef = { featId, featureId ->
                    db.featFeatureCrossRefQueries.delete(
                        owner = null,
                        featId = featId.toLong(),
                        featureId = featureId.toLong()
                    )
                    db.featFeatureCrossRefQueries.insert(
                        featId = featId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                },
                insertFeatChoice = {
                    db.featChoicesQueries.delete(
                        owner = null,
                        id = it.id.toLong()
                    )
                    db.featChoicesQueries.insert(
                        id = it.id.toLong(),
                        name = it.name,
                        choose = it.choose.toLong(),
                        owner = null
                    )
                }
            ),
            abilitiesManager = AbilitiesManager(
                postAbilitiesToSkills = {
                    abilitiesToSkills = it
                },
                getAbilitiesToSkills = {
                    abilitiesToSkills
                }
            ),
            itemManager = ItemManager(
                getAll = { items },
                getSimpleWeapons = { simpleWeapons },
                getMartialWeapons = { martialWeapons },
                getArmors = { armors },
                postMartialWeapons = {
                    martialWeapons = it
                },
                postSimpleWeapons = { simpleWeapons = it },
                postArmors = { armors = it },
                postMisc = { miscItems = it }
            ),
            languageManager = LanguageManager(
                getAllLanguages = { languages },
                postAll = { languages += it }
            ),
            spellManager = SpellManager(
                getSpellIdByName = {
                    db.spellsQueries.selectIdByName(it).executeAsOne().toInt()
                },
                insertSpell = {
                    db.spellsQueries.delete(it.id.toLong(), null)
                    db.spellsQueries.insertSpell(
                        spells = Spells(
                            id = it.id.toLong(),
                            name = it.name,
                            level = it.level.toLong(),
                            components = it.components.toJsonList(),
                            itemComponents = it.itemComponents.toJsonList(),
                            school = it.school,
                            desc = it.desc,
                            range = it.range,
                            area = it.area,
                            castingTime = it.castingTime,
                            duration = it.duration,
                            classes = it.classes.toJsonList(),
                            damage = it.damage,
                            isRitual = it.isRitual,
                            isHomebrew = if (it.isHomebrew) 1 else 0,
                            owner = null
                        )
                    )
                    it.id
                }
            ),
            classManager = ClassManager(
                insertClassSpellCrossRef = { classId, spellId ->
                    db.classSpellCrossRefQueries.delete(
                        owner = null,
                        spellId = spellId.toLong(),
                        classId = classId.toLong()
                    )
                    db.classSpellCrossRefQueries.insert(
                        classId = classId.toLong(),
                        spellId = spellId.toLong(),
                        owner = null
                    )
                },
                insertClassSubclassId = { classId, subclassId ->
                    db.classSubclassCrossRefQueries.deleteBy(
                        classId = classId.toLong(),
                        subclassId = subclassId.toLong(),
                        owner = null
                    )
                    db.classSubclassCrossRefQueries.insert(
                        classId = classId.toLong(),
                        subclassId = subclassId.toLong(),
                        owner = null
                    )
                },
                insertClass = {
                    db.classesQueries.deleteClass(
                        id = it.id.toLong(),
                        owner = null
                    )
                    db.classesQueries.insertClass(
                        classes = Classes(
                            id = it.id.toLong(),
                            name = it.name,
                            hitDie = it.hitDie.toLong(),
                            subclassLevel = it.subclassLevel.toLong(),
                            proficiencyChoices = it.proficiencyChoices.toJsonList(),
                            proficiencies = it.proficiencies.toJsonList(),
                            equipmentChoices = it.equipmentChoices.toJsonList(),
                            equipment = it.equipment.toJsonList(),
                            spellCasting = it.spellCasting?.let{ jsonObjectAdapter.decode(gsonInstance.toJson(it))},
                            pactMagic = it.pactMagic?.let{ jsonObjectAdapter.decode(gsonInstance.toJson(it))},
                            startingGoldD4s = it.startingGoldD4s.toLong(),
                            startingGoldMultiplier = it.startingGoldMultiplier.toLong(),
                            isHomebrew = 0,
                            owner = null,
                        )
                    )
                },
                insertClassFeatureCrossRef = { id, featureId ->
                    db.classFeatureCrossRefQueries.delete(
                        owner = null,
                        classId = id.toLong(),
                        featureId = featureId.toLong()
                    )
                    db.classFeatureCrossRefQueries.insert(
                        featureId = featureId.toLong(),
                        id = id.toLong(),
                        owner = null
                    )
                }
            ),
            logManager = LogManager(
                logError = {
                    println("logManager E: $it")
                }
            ),
            backgroundManager = BackgroundManager(
                insertBackgroundSpellCrossRef = { backgroundId, spellId ->
                    db.backgroundSpellCrossRefQueries.deleteByBackgroundSpellCrossRef(
                        backgroundId = backgroundId.toLong(),
                        spellId = spellId.toLong(),
                        owner = null
                    )
                    db.backgroundSpellCrossRefQueries.insert(
                        backgroundId = backgroundId.toLong(),
                        spellId = spellId.toLong(),
                        owner = null
                    )
                },
                insertBackground = {
                    db.backgroundsQueries.delete(
                        id = it.id.toLong(),
                        owner = null
                    )
                    db.backgroundsQueries.insert(
                        Backgrounds(
                            id = it.id.toLong(),
                            name = it.name,
                            desc = it.desc,
                            spells = gsonInstance.toJson(it.spells),
                            proficiencies = it.proficiencies.toJsonList(),
                            proficiencyChoices = it.proficiencyChoices?.toJsonList(),
                            languages = it.languages.toJsonList(),
                            languageChoices = it.languageChoices?.toJsonList(),
                            equipment = it.equipment.toJsonList(),
                            equipmentChoices = it.equipmentChoices.toJsonList(),
                            isHomebrew = 0,
                            owner = null
                        )
                    )
                },
                insertBackgroundFeatureCrossRef = { backgroundId, featureId ->
                    db.backgroundFeatureCrossRefQueries.delete(
                        backgroundId = backgroundId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                    db.backgroundFeatureCrossRefQueries.insert(
                        backgroundId = backgroundId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                }
            ),
            raceManager = RaceManager(
                insertRace = {
                    db.racesQueries.delete(
                        owner = null,
                        raceId = it.raceId.toLong()
                    )
                    db.racesQueries.insertRace(
                        Races(
                            raceId = it.raceId.toLong(),
                            raceName = it.raceName,
                            groundSpeed = it.groundSpeed.toLong(),
                            abilityBonuses = it.abilityBonuses?.toJsonList(),
                            alignment = it.alignment,
                            age = it.age,
                            size = it.size,
                            sizeDesc = it.sizeDesc,
                            startingProficiencies = it.startingProficiencies.toJsonList(),
                            proficiencyChoices = it.proficiencyChoices.toJsonList(),
                            languages = it.languages.toJsonList(),
                            languageChoices = it.languageChoices.toJsonList(),
                            languageDesc = it.languageDesc,
                            isHomebrew = 0,
                            abcchoose = it.abilityBonusChoice?.choose?.toLong(),
                            abcfrom = gsonInstance.toJson(it.abilityBonusChoice?.from),
                            abcmaxOccurrencesOfAbility = it.abilityBonusChoice?.maxOccurrencesOfAbility?.toLong(),
                            abcchosenByString = it.abilityBonusChoice?.chosenByString.serialize(),
                            owner = null
                        )
                    )
                },
                insertRaceSubraceCrossRef = { subraceId, raceId ->
                    db.raceSubraceCrossRefQueries.delete(
                        owner = null,
                        subraceId = subraceId.toLong(),
                        raceId = raceId.toLong()
                    )
                    db.raceSubraceCrossRefQueries.insert(
                        subraceId = subraceId.toLong(),
                        raceId = raceId.toLong(),
                        owner = null
                    )
                },
                insertRaceFeatureCrossRef = { raceId, featureId ->
                    db.raceFeatureCrossRefQueries.delete(
                        featureId = featureId.toLong(),
                        raceId = raceId.toLong(),
                        owner = null
                    )
                    db.raceFeatureCrossRefQueries.insert(
                        featureId = featureId.toLong(),
                        raceId = raceId.toLong(),
                        owner = null
                    )
                }
            ),
            subraceManager = SubraceManager(
                insertSubrace = {
                    db.subracesQueries.delete(
                        owner = null,
                        id = it.id.toLong()
                    )
                    db.subracesQueries.insert(
                        Subraces(
                            id = it.id.toLong(),
                            name = it.name,
                            abilityBonuses = it.abilityBonuses?.toJsonList(),
                            abilityBonusChoice = it.abilityBonusChoice?.toJsonObject(),
                            startingProficiencies = it.startingProficiencies?.toJsonList(),
                            languages = it.languages.toJsonList(),
                            languageChoices = it.languageChoices.toJsonList(),
                            size = it.size,
                            groundSpeed = it.groundSpeed?.toLong(),
                            isHomebrew = false,
                            owner = null
                        )
                    )
                },
                insertSubraceFeatureCrossRef = { subraceId, featureId ->
                    db.subraceFeatureCrossRefQueries.delete(
                        subraceId = subraceId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                    db.subraceFeatureCrossRefQueries.insert(
                        subraceId = subraceId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                },
                insertSubraceFeatChoiceCrossRef = { featChoiceId, subraceId ->
                    db.subraceFeatChoiceCrossRefQueries.delete(
                        subraceId = subraceId.toLong(),
                        featChoiceId = featChoiceId.toLong(),
                        owner = null
                    )
                    db.subraceFeatChoiceCrossRefQueries.insert(
                        subraceId = subraceId.toLong(),
                        featChoiceId = featChoiceId.toLong(),
                        owner = null
                    )
                }
            ),
            subclassManager = SubclassManager(
                insertSubclass = {
                    db.subclassesQueries.delete(
                        owner = null,
                        subclassId = it.subclassId.toLong()
                    )
                    db.subclassesQueries.insert(
                        Subclasses(
                            subclass_name = it.name,
                            subclass_spell_casting = if(it.spellCasting == null) null else it.spellCasting?.toJsonObject(),
                            subclass_isHomebrew = 9,
                            subclassId = it.subclassId.toLong(),
                            spellAreFree = it.spellAreFree,
                            owner = null
                        )
                    )
                },
                insertSubclassFeatureCrossRef = { featureId, subclassId ->
                    db.subclassFeatureCrossRefQueries.delete(
                        subclassId = subclassId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                    db.subclassFeatureCrossRefQueries.insert(
                        subclassId = subclassId.toLong(),
                        featureId = featureId.toLong(),
                        owner = null
                    )
                },
                insertSubclassSpellCrossRef = { subclassId, spellId ->
                    db.subclassSpellCrossRefQueries.delete(
                        subclassId = subclassId.toLong(),
                        spellId = spellId.toLong(),
                        owner = null
                    )
                    db.subclassSpellCrossRefQueries.insert(
                        subclassId = subclassId.toLong(),
                        spellId = spellId.toLong(),
                        owner = null
                    )
                }
            )
        )
        println("Model update initiated")
        with(dataSource) {
            GlobalScope.launch { generateSkills();  generateItems(); println("items generated") }.invokeOnCompletion {
                items.addAll(miscItems)
                items.addAll(armors)
                items.addAll(simpleWeapons)
                items.addAll(martialWeapons)

                println("beginning transaction")
                db.transaction {
                    GlobalScope.launch {
                        updateSpells()

                        generateSkills()

                        generateItemProficiencies()

                        generateLanguages()

                        generateInfusions()

                        generateInvocations()

                        generateMetaMagic()

                        generateManeuvers()

                        generateFightingStyles()

                        generateFeats()

                        updateBackgrounds()

                        updateRaces()

                        updateClasses()
                        println("Model updated")
                    }
                }
            }
        }
    }
}