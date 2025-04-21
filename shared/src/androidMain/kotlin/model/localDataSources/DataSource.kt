package model.localDataSources

import LogManager
import SharedDataSource
import StringFileResolver
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.room.withTransaction
import dataManagers.*
import gmail.loganchazdon.dndhelper.shared.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.*
import model.database.RoomDataBase
import model.database.daos.*
import org.jetbrains.compose.resources.ExperimentalResourceApi


@Suppress("PropertyName")
actual interface DataSource {
    actual fun getItems(): Flow<List<ItemInterface>>
    actual fun getAbilitiesToSkills(): Flow<Map<String, List<String>>>
    actual fun getLanguages(): Flow<List<Language>>
    actual fun getMetaMagics(): Flow<List<Metamagic>>
    actual fun getArmors(): Flow<List<Armor>>
    actual fun getMiscItems(): Flow<List<ItemInterface>>
    actual fun getMartialWeapons(): Flow<List<Weapon>>
    actual fun getInfusions(): Flow<List<Infusion>>
    actual fun getSimpleWeapons(): Flow<List<Weapon>>
    actual fun getInvocations(): Flow<List<Feature>>
}

@ExperimentalResourceApi
class LocalDataSourceImpl constructor(
    val context: Context,
    val db: RoomDataBase,
    val featureDao: FeatureDao,
    val backgroundDao: BackgroundDao,
    val classDao: ClassDao,
    val featDao: FeatDao,
    val raceDao: RaceDao,
    val spellDao: SpellDao,
    val subclassDao: SubclassDao,
    val subraceDao: SubraceDao
) : DataSource {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val _infusions: MutableLiveData<List<Infusion>> =
        MutableLiveData()
    private val _invocations: MutableLiveData<List<Feature>> =
        MutableLiveData()
    private val _martialWeapons: MutableLiveData<List<Weapon>> =
        MutableLiveData()
    private val _simpleWeapons: MutableLiveData<List<Weapon>> =
        MutableLiveData()
    private val _miscItems: MutableLiveData<List<ItemInterface>> =
        MutableLiveData()
    private val _armors: MutableLiveData<List<Armor>> =
        MutableLiveData()
    private val _items: MediatorLiveData<List<ItemInterface>> =
        MediatorLiveData()
    private val _abilitiesToSkills: MutableLiveData<Map<String, List<String>>> =
        MutableLiveData()
    private val _languages: MutableLiveData<List<Language>> =
        MutableLiveData()
    private val _metaMagics: MutableLiveData<List<Metamagic>> =
        MutableLiveData()
    private val _maneuvers: MutableLiveData<List<Feature>> =
        MutableLiveData()

    override fun getItems(): Flow<List<ItemInterface>> =
        _items.asFlow()

    override fun getAbilitiesToSkills(): Flow<Map<String, List<String>>> =
        _abilitiesToSkills.asFlow()


    override fun getLanguages(): Flow<List<Language>> =
        _languages.asFlow()

    override fun getMetaMagics(): Flow<List<Metamagic>> =
        _metaMagics.asFlow()

    override fun getArmors(): Flow<List<Armor>> =
        _armors.asFlow()

    override fun getMiscItems(): Flow<List<ItemInterface>> =
        _miscItems.asFlow()

    override fun getMartialWeapons(): Flow<List<Weapon>> =
        _martialWeapons.asFlow()

    override fun getInfusions(): Flow<List<Infusion>> =
        _infusions.asFlow()

    override fun getSimpleWeapons(): Flow<List<Weapon>> =
        _simpleWeapons.asFlow()

    override fun getInvocations(): Flow<List<Feature>> =
        _invocations.asFlow()

    init {
        scope.launch(Dispatchers.IO) {
            with(
                SharedDataSource(
                    res = StringFileResolver(
                        getString = {
                            Res.readBytes("files/raw/$it").decodeToString()
                        }
                    ),
                    featureManager = FeatureManager(
                        insertFeature = { featureDao.insertFeature(it) },
                        _insertFeatureChoiceIndexCrossRef = { choiceId, index, levels, classes, schools ->
                            featureDao.insertFeatureChoiceIndexCrossRef(
                                FeatureChoiceIndexCrossRef(
                                    choiceId = choiceId,
                                    index = index,
                                    levels = levels,
                                    classes = classes,
                                    schools = schools,
                                )
                            )
                        },
                        insertFeatureOptionsCrossRef = { featureId, choiceId ->
                            featureDao.insertFeatureOptionsCrossRef(
                                featureId = featureId,
                                id = choiceId
                            )
                        },
                        insertFeatureChoice = {
                            featureDao.insertFeatureChoice(it)
                        },
                        insertIndexRef = { ids, index ->
                            featureDao.insertIndexRef(
                                index = index,
                                ids = ids
                            )
                        },
                        postManeuvers = {
                            _maneuvers.postValue(it)
                        },
                        postInvocations = {
                            _invocations.postValue(it)
                        },
                        postInfusions = { _infusions.postValue(it) },
                        insertFeatureSpellCrossRef = { spellId, featureId ->
                            featureDao.insertFeatureSpellCrossRef(
                                FeatureSpellCrossRef(
                                    spellId = spellId,
                                    featureId = featureId
                                )
                            )
                        },
                        insertOptionsFeatureCrossRef = { featureId, choiceId ->
                            featureDao.insertOptionsFeatureCrossRef(
                                OptionsFeatureCrossRef(
                                    featureId = featureId,
                                    choiceId = choiceId
                                )
                            )
                        },
                        getFeatureIdByName = { name ->
                            val cursor = db.query(
                                "SELECT featureId FROM features WHERE LOWER(features.name) LIKE LOWER('$name')",
                                arrayOf()
                            )
                            cursor.moveToFirst()
                            cursor.getInt(0)
                        }
                    ),
                    metaMagicManager = MetaMagicManager(
                        postAll = { _metaMagics.postValue(it) }
                    ),
                    featManager = FeatManager(
                        insertFeat = {
                            featDao.insertFeat(it.asTable())
                        },
                        insertFeatFeatureCrossRef = { featId, featureId ->
                            featDao.insertFeatFeatureCrossRef(
                                FeatFeatureCrossRef(
                                    featId = featId,
                                    featureId = featureId
                                ),

                                )
                        },
                        insertFeatChoice = {
                            featDao.insertFeatChoice(it)
                        }
                    ),
                    abilitiesManager = AbilitiesManager(
                        postAbilitiesToSkills = { _abilitiesToSkills.postValue(it) },
                        getAbilitiesToSkills = { _abilitiesToSkills.value ?: emptyMap() }
                    ),
                    itemManager = ItemManager(
                        getAll = { _items.value ?: emptyList() },
                        getSimpleWeapons = { _simpleWeapons.value ?: emptyList() },
                        getMartialWeapons = { _martialWeapons.value ?: emptyList() },
                        getArmors = { _armors.value ?: emptyList() },
                        postMartialWeapons = { _martialWeapons.postValue(it) },
                        postSimpleWeapons = { _simpleWeapons.postValue(it) },
                        postArmors = { _armors.postValue(it) },
                        postMisc = { _miscItems.postValue(it) }
                    ),
                    languageManager = LanguageManager(
                        getAllLanguages = { _languages.value ?: emptyList() },
                        postAll = { _languages.postValue(it) }
                    ),
                    spellManager = SpellManager(
                        getSpellIdByName = {
                            val cursor = db.query(
                                "SELECT id FROM spells WHERE LOWER(spells.name) LIKE LOWER(\'${
                                    it.replace("'", "_")
                                }\')",
                                arrayOf()
                            )
                            cursor.moveToFirst()
                            cursor.getInt(0)
                        },
                        insertSpell = { spellDao.insertSpell(it) }
                    ),
                    classManager = ClassManager(
                        insertClassSpellCrossRef = { classId, spellId ->
                            classDao.insertClassSpellCrossRef(
                                ClassSpellCrossRef(
                                    classId = classId,
                                    spellId = spellId
                                )
                            )
                        },
                        insertClassSubclassId = { classId, subclassId ->
                            classDao.insertClassSubclassId(
                                ClassSubclassCrossRef(
                                    classId = classId,
                                    subclassId = subclassId
                                )
                            )
                        },
                        insertClass = { classDao.insertClass(it) },
                        insertClassFeatureCrossRef = { id, featureId ->
                            classDao.insertClassFeatureCrossRef(
                                ClassFeatureCrossRef(
                                    featureId = featureId,
                                    id = id
                                )
                            )
                        }
                    ),
                    logManager = LogManager(
                        logError = { println(it) }
                    ),
                    backgroundManager = BackgroundManager(
                        insertBackgroundSpellCrossRef = { backgroundId, spellId ->
                            backgroundDao.insertBackgroundSpellCrossRef(
                                BackgroundSpellCrossRef(
                                    backgroundId = backgroundId,
                                    spellId = spellId
                                )
                            )
                        },
                        insertBackground = {
                            backgroundDao.insertBackground(it)
                        },
                        insertBackgroundFeatureCrossRef = { backgroundId, featureId ->
                            backgroundDao.insertBackgroundFeatureCrossRef(
                                BackgroundFeatureCrossRef(
                                    backgroundId = backgroundId,
                                    featureId = featureId
                                )
                            )
                        }
                    ),
                    raceManager = RaceManager(
                        insertRace = { raceDao.insertRace(it) },
                        insertRaceSubraceCrossRef = { subraceId, raceId ->
                            raceDao.insertRaceSubraceCrossRef(
                                RaceSubraceCrossRef(
                                    subraceId = subraceId,
                                    raceId = raceId
                                )
                            )
                        },
                        insertRaceFeatureCrossRef = { raceId, featureId ->
                            raceDao.insertRaceFeatureCrossRef(
                                RaceFeatureCrossRef(
                                    featureId = featureId,
                                    raceId = raceId
                                )
                            )
                        }
                    ),
                    subraceManager = SubraceManager(
                        insertSubrace = { subraceDao.insertSubrace(it) },
                        insertSubraceFeatureCrossRef = { subraceId, featureId ->
                            subraceDao.insertSubraceFeatureCrossRef(
                                SubraceFeatureCrossRef(
                                    subraceId = subraceId,
                                    featureId = featureId
                                )
                            )
                        },
                        insertSubraceFeatChoiceCrossRef = { featChoiceId, subraceId ->
                            subraceDao.insertSubraceFeatChoiceCrossRef(
                                SubraceFeatChoiceCrossRef(
                                    subraceId = subraceId,
                                    featChoiceId = featChoiceId
                                )
                            )
                        }
                    ),
                    subclassManager = SubclassManager(
                        insertSubclass = { subclassDao.insertSubclass(it) },
                        insertSubclassFeatureCrossRef = { featureId, subclassId ->
                            subclassDao.insertSubclassFeatureCrossRef(
                                SubclassFeatureCrossRef(
                                    subclassId = subclassId,
                                    featureId = featureId
                                )
                            )
                        },
                        insertSubclassSpellCrossRef = { subclassId, spellId ->
                            subclassDao.insertSubclassSpellCrossRef(
                                SubclassSpellCrossRef(
                                    subclassId = subclassId,
                                    spellId = spellId
                                )
                            )
                        }
                    )
                )
            ) {
                scope.launch(Main) {
                    //Anonymous function to pass to add source.
                    val addData = fun(value: List<ItemInterface>) {
                        if (_items.value == null) {
                            _items.setValue(value)
                        } else {
                            _items.setValue(_items.value?.plus(value))
                        }
                    }

                    _items.addSource(_martialWeapons) { value -> addData(value) }
                    _items.addSource(_simpleWeapons) { value -> addData(value) }
                    _items.addSource(_armors) { value -> addData(value) }
                    _items.addSource(_miscItems) { value -> addData(value) }

                    //This is needed because if _items is not observed its data will not be directly accessible until it is.
                    //So in order to check it for item indexes we need an empty observer.
                    scope.launch(Main) {
                        _items.observeForever {

                        }
                    }

                    generateItems()
                }.invokeOnCompletion {
                    scope.launch(Dispatchers.IO) {
                        db.withTransaction {
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
                        }
                    }
                }
            }
        }
    }
}
