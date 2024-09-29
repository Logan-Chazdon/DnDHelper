package ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import model.*
import model.junctionEntities.ClassFeatureCrossRef
import model.junctionEntities.ClassSubclassCrossRef
import model.repositories.ClassRepository
import model.repositories.FeatureRepository
import model.repositories.SpellRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomebrewClassViewModel constructor(
    private val classRepository: ClassRepository,
    private val featureRepository: FeatureRepository,
    private val spellRepository: SpellRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        classRepository.insertClassFeatureCrossRef(
            ClassFeatureCrossRef(
                id = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun saveClass() {
        var pactMagic: PactMagic? = null
        if (hasPactMagic.value) {
            val pactSlots = mutableListOf<Resource>()
            pactMagicSlots.forEach { (levelString, amountString) ->
                val level = try {
                    levelString.toInt()
                } catch (E: java.lang.Exception) {
                    1
                }
                val amount = try {
                    amountString.toInt()
                } catch (E: java.lang.Exception) {
                    1
                }
                pactSlots.add(
                    Resource(
                        name = SpellRepository.allSpellLevels[level - 1].second,
                        currentAmount = amount,
                        maxAmountType = amount.toString(),
                        rechargeAmountType = amount.toString()
                    )
                )
            }

            val cantripsKnown = mutableListOf<Int>()
            val spellsKnown = mutableListOf<Int>()
            pactMagicCantripsKnown.forEachIndexed { index, it ->
                cantripsKnown.add(
                    index, try {
                        it.toInt()
                    } catch (e: java.lang.Exception) {
                        cantripsKnown.getOrNull(index - 1) ?: 0
                    }
                )
            }

            pactMagicSpellsKnown.forEachIndexed { index, it ->
                spellsKnown.add(
                    index, try {
                        it.toInt()
                    } catch (e: java.lang.Exception) {
                        spellsKnown.getOrNull(index - 1) ?: 0
                    }
                )
            }

            pactMagic = PactMagic(
                castingAbility = pactMagicAbility.value.substring(0, 3),
                spellsKnown = spellsKnown,
                cantripsKnown = cantripsKnown,
                pactSlots = pactSlots
            )
        }

        var spellCasting: SpellCasting? = null
        if (hasSpellCasting.value) {
            val spellKnown = if (spellCastingLearnsSpells.value) {
                mutableListOf<Int>()
            } else {
                null
            }
            val cantripsKnown = mutableListOf<Int>()


            var levelSpellsKnown = 0
            var levelCantripsKnown = 0
            var currentIndex = 0
            for (currentLevel in 1 until 21) {
                if (
                    (spellCastingSpellsAndCantripsKnown.keys.elementAtOrNull(currentIndex)
                        ?.toIntOrNull() ?: 0) <= currentLevel
                ) {
                    levelSpellsKnown =
                        spellCastingSpellsAndCantripsKnown[currentLevel.toString()]?.second?.toIntOrNull()
                            ?: spellKnown?.getOrNull(currentLevel - 1) ?: 0

                    levelCantripsKnown =
                        spellCastingSpellsAndCantripsKnown[currentLevel.toString()]?.first?.toIntOrNull()
                            ?: cantripsKnown.getOrNull(currentLevel - 1) ?: 0
                    currentIndex += 1
                }

                spellKnown?.add(currentLevel - 1, levelSpellsKnown)
                cantripsKnown.add(currentLevel - 1, levelCantripsKnown)
            }

            spellCasting = SpellCasting(
                type = if (spellCastingIsHalfCaster.value) {
                    0.5
                } else {
                    1.0
                },
                hasSpellBook = false, //TODO
                castingAbility = spellCastingAbility.value.substring(0, 3),
                prepareFrom = if (spellCastingPrepares.value) {
                    if (spellCastingLearnsSpells.value) {
                        "known"
                    } else {
                        "all"
                    }
                } else {
                    null
                },
                preparationModMultiplier = if (spellCastingPrepares.value) {
                    spellCastingCastingModMulti.value.toDoubleOrNull()
                } else {
                    null
                },
                spellsKnown = spellKnown,
                cantripsKnown = cantripsKnown
            )
        }

        classRepository.insertClass(
            ClassEntity(
                name = name.value,
                isHomebrew = true,
                hitDie = try {
                    hitDie.value.toInt()
                } catch (e: Exception) {
                    8
                },
                subclassLevel = try {
                    subclassLevel.value.toInt()
                } catch (e: Exception) {
                    1
                },
                proficiencyChoices = emptyList(), //TODO
                proficiencies = emptyList(), //TODO
                equipmentChoices = emptyList(), //TODO
                equipment = emptyList(), //TODO
                startingGoldD4s = try {
                    goldDie.value.toInt()
                } catch (e: Exception) {
                    4
                },
                startingGoldMultiplier = try {
                    goldMultiplier.value.toInt()
                } catch (e: Exception) {
                    10
                },
                spellCasting = spellCasting,
                pactMagic = pactMagic,
                id = id
            )
        )
    }

    fun deleteSubclass(it: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            classRepository.removeClassSubclassCrossRef(
                ClassSubclassCrossRef(
                    classId = id,
                    subclassId = subclasses!!.value!![it].subclassId
                )
            )
        }
    }

    fun removeFeature(featureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            classRepository.removeClassFeatureCrossRef(
                ClassFeatureCrossRef(
                    featureId = featureId,
                    id = id
                )
            )
        }
    }

    fun createDefaultSubclass(): Int {
        val subclassId = classRepository.createDefaultSubclass()
        classRepository.insertClassSubclassCrossRef(
            ClassSubclassCrossRef(
                classId = id,
                subclassId = subclassId
            )
        )
        return subclassId
    }

    val pactMagicSpellsKnown =
        mutableStateListOf(
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "11",
            "12",
            "12",
            "13",
            "13",
            "14",
            "14",
            "15",
            "15",
            "15"
        )
    val pactMagicCantripsKnown =
        mutableStateListOf(
            "2",
            "2",
            "2",
            "3",
            "3",
            "3",
            "3",
            "3",
            "3",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4",
            "4"
        )
    val allSpells = spellRepository.getLiveSpells()
    val hasSpellCasting = mutableStateOf(false)
    val hasPactMagic = mutableStateOf(false)
    val goldMultiplier = mutableStateOf("10")
    val goldDie = mutableStateOf("4")
    val hitDie = mutableStateOf("8")
    var subclasses: LiveData<List<Subclass>>? = null
    val name = mutableStateOf("")
    var clazz: MediatorLiveData<Class> = MediatorLiveData()
    var id: Int = -1
    val subclassLevel = mutableStateOf("1")
    val pactMagicAbility = mutableStateOf("Charisma")
    val spellCastingAbility = mutableStateOf("Intelligence")
    val pactMagicSpells = mutableStateListOf<Spell>()
    val spellCastingSpells = mutableStateListOf<Spell>()
    val spellCastingSlots = mutableStateListOf(
        arrayOf("2", "0", "0", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("3", "0", "0", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("4", "2", "0", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "0", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "2", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "0", "0", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "1", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "2", "0", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "1", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "1", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "0", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "1", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "1", "0", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "1", "1", "0", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "1", "1", "1", "0"),
        arrayOf("4", "3", "3", "3", "3", "2", "1", "1", "1", "1"),
        arrayOf("4", "3", "3", "3", "3", "3", "1", "1", "1", "1"),
        arrayOf("4", "3", "3", "3", "3", "3", "2", "1", "1", "1"),
        arrayOf("4", "3", "3", "3", "3", "3", "2", "2", "1", "1"),
    )
    val spellCastingPrepares = mutableStateOf(false)
    val spellCastingLearnsSpells = mutableStateOf(false)
    val spellCastingCastingModMulti = mutableStateOf("1")
    val spellCastingLevelMulti = mutableStateOf("1")
    val spellCastingIsHalfCaster = mutableStateOf(false)
    val spellCastingSpellsAndCantripsKnown = mutableStateMapOf(
        "1" to Pair("4", "2"),
        "20" to Pair("4", "6")
    )

    //Slot level to slot number
    val pactMagicSlots = mutableStateListOf(
        "1" to "1",
        "1" to "2",
        "2" to "2",
        "2" to "2",
        "3" to "2",
        "3" to "2",
        "4" to "2",
        "4" to "2",
        "5" to "2",
        "5" to "2",
        "5" to "3",
        "5" to "3",
        "5" to "3",
        "5" to "3",
        "5" to "3",
        "5" to "3",
        "5" to "3",
        "5" to "4",
        "5" to "4",
        "5" to "4",
        "5" to "4",
    )


    init {
        viewModelScope.async(Dispatchers.IO) {
            savedStateHandle.get<String>("id")!!.toInt().let {
                id = if (it == -1) {
                    classRepository.createDefaultClass()
                } else {
                    it
                }

            }
        }.invokeOnCompletion {
            val source = classRepository.getClass(id)
            clazz.addSource(source) { newClazz ->
                clazz.value = newClazz

                //Set all data in the viewModel to match the class.
                name.value = newClazz.name

                if (!newClazz.pactMagic?.pactSlots.isNullOrEmpty()) {
                    pactMagicSlots.clear()
                    newClazz.pactMagic!!.pactSlots.forEach { resource ->
                        pactMagicSlots.add(
                            Pair(
                                SpellRepository.allSpellLevels.first { it.second == resource.name }.first.toString(),
                                resource.maxAmountType
                            )
                        )
                    }
                }

                if (newClazz.spellCasting?.spellsKnown != null || newClazz.spellCasting?.cantripsKnown != null) {
                    spellCastingSpellsAndCantripsKnown.clear()
                    var previousSpell = ""
                    var previousCantrip = ""

                    for (level in 1..20) {
                        val spells = newClazz.spellCasting.spellsKnown?.get(level - 1).toString()
                        val cantrips =
                            newClazz.spellCasting.cantripsKnown?.get(level - 1).toString()

                        if (spells != previousSpell || cantrips != previousCantrip) {
                            spellCastingSpellsAndCantripsKnown[level.toString()] =
                                Pair(cantrips, spells)
                            previousSpell = spells
                            previousCantrip = cantrips
                        }
                    }
                }

                spellCastingIsHalfCaster.value = newClazz.spellCasting?.type == 0.5
                spellCastingCastingModMulti.value =
                    newClazz.spellCasting?.preparationModMultiplier.toString()
                spellCastingLearnsSpells.value = newClazz.spellCasting?.prepareFrom == "known"
                spellCastingPrepares.value = newClazz.spellCasting?.prepareFrom != null
                newClazz.spellCasting?.spellSlotsByLevel?.let { slots ->
                    if (slots.isNotEmpty()) {
                        spellCastingSlots.clear()
                        slots.forEach { resources: List<Resource> ->
                            val tempArray = emptyArray<String>()
                            resources.forEach {
                                tempArray.plus(it.maxAmountType)
                            }
                            spellCastingSlots.add(tempArray)
                        }
                    }
                }

                if (newClazz.spellCasting?.known?.isNotEmpty() == true) {
                    spellCastingSpells.clear()
                    spellCastingSpells.addAll(newClazz.spellCasting.known.map { it.first })
                }

                newClazz.pactMagic?.known?.let { known -> pactMagicSpells.addAll(known) }

                newClazz.pactMagic?.castingAbility?.let { pactMagicAbility.value = it }
                newClazz.spellCasting?.castingAbility?.let { spellCastingAbility.value = it }

                subclassLevel.value = newClazz.subclassLevel.toString()
                hitDie.value = newClazz.hitDie.toString()
                goldDie.value = newClazz.startingGoldD4s.toString()
                goldMultiplier.value = newClazz.startingGoldMultiplier.toString()
                hasSpellCasting.value = newClazz.spellCasting != null
                hasPactMagic.value = newClazz.pactMagic != null

                if (hasPactMagic.value) {
                    pactMagicCantripsKnown.clear()
                    pactMagicCantripsKnown.addAll(newClazz.pactMagic?.cantripsKnown?.map { it.toString() }
                        ?: emptyList())

                    pactMagicSpellsKnown.clear()
                    pactMagicSpellsKnown.addAll(newClazz.pactMagic?.spellsKnown?.map { it.toString() }
                        ?: emptyList())
                }

                clazz.removeSource(source)
            }
            subclasses = classRepository.getSubclassesByClassId(id)
        }
    }
}