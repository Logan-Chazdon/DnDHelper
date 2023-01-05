package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSubclassCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomebrewClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = repository.createDefaultFeature()!!
        repository.insertClassFeatureCrossRef(
            ClassFeatureCrossRef(
                id = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun saveClass() {
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
                    name = Repository.allSpellLevels[level - 1].second,
                    currentAmount = amount,
                    maxAmountType = amount.toString(),
                    rechargeAmountType = amount.toString()
                )
            )
        }

        val cantripsKnown = mutableListOf<Int>()
        val spellsKnown = mutableListOf<Int>()
        pactMagicCantripsKnown.forEachIndexed { index, it ->
            cantripsKnown.add(index, try {
                it.toInt()
            } catch (e: java.lang.Exception) {
                cantripsKnown.getOrNull(index - 1) ?: 0
            })
        }

        pactMagicSpellsKnown.forEachIndexed { index, it ->
            spellsKnown.add(index, try {
                it.toInt()
            } catch (e: java.lang.Exception) {
                spellsKnown.getOrNull(index - 1) ?: 0
            })
        }


        val pactMagic = PactMagic(
            castingAbility = pactMagicAbility.value.substring(0, 3),
            spellsKnown = spellsKnown,
            cantripsKnown = cantripsKnown,
            pactSlots = pactSlots
        )

        repository.insertClass(
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
                spellCasting = null, //TODO
                pactMagic = pactMagic
            )
        )
    }

    fun deleteSubclass(it: Int) {
        repository.removeClassSubclassCrossRef(
            ClassSubclassCrossRef(
                classId = id,
                subclassId = subclasses!!.value!![it].subclassId
            )
        )
    }

    fun removeFeature(featureId: Int) {
        repository.removeClassFeatureCrossRef(
            ClassFeatureCrossRef(
                featureId = featureId,
                id = id
            )
        )
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
            "4"
        )
    val allSpells = repository.getLiveSpells()
    val hasPactMagic = mutableStateOf(false)
    val goldMultiplier = mutableStateOf("10")
    val goldDie = mutableStateOf("4")
    val hitDie = mutableStateOf("8")
    var subclasses: LiveData<List<Subclass>>? = null
    val name = mutableStateOf("")
    var clazz: LiveData<Class>? = null
    var id: Int = -1
    val subclassLevel = mutableStateOf("1")
    val pactMagicAbility = mutableStateOf("Charisma")
    val pactMagicSpells = mutableStateListOf<Spell>()


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
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.get<String>("id")!!.toInt().let {
                if (it == -1) {
                    id = repository.createDefaultClass()
                }
                clazz = repository.getClass(it)
                id = it
                subclasses = repository.getSubclassesByClassId(id)
            }
        }
    }
}