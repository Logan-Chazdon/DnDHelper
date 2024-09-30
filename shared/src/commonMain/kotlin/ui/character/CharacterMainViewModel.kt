package ui.character



import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import model.Character
import model.Feature
import model.Infusion
import model.ItemInterface
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@OptIn(FlowPreview::class)
@KoinViewModel
class CharacterMainViewModel constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CharacterRepository,
): ViewModel() {
    private val debounceTime: Long = 1000
    val characterFeatures: MutableStateFlow<List<Pair<Int, Feature>>> = MutableStateFlow(emptyList())
    var character : MutableStateFlow<Character> = MutableStateFlow(Character())

    //Character Information.
    val name = MutableStateFlow("")
    val ideals = MutableStateFlow("")
    val personalityTraits = MutableStateFlow("")
    val bonds = MutableStateFlow("")
    val flaws = MutableStateFlow("")
    val notes = MutableStateFlow("")

    private val dataToPersistenceFunction = mapOf(
        name to ::setName,
        personalityTraits to ::setPersonalityTraits,
        bonds to ::setBonds,
        flaws to ::setFlaws,
        notes to ::setNotes,
        ideals to ::setIdeals
    )

    init {
        repository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )


        //Call the persistence function of state flow after the user types
        //and then doesn't type for debounce time.
        for (it in dataToPersistenceFunction) {
            viewModelScope.launch(/*Dispatchers.IO*/) {
                it.key.debounce(debounceTime)
                    .collect(it.value)
            }
        }
    }



    suspend fun longRest() {
        character.value.longRest()
        repository.insertCharacter(character.value)
        character.value.classes.forEach {
            it.value.pactMagic?.pactSlots?.get(it.value.level)?.let { it1 ->
                repository.insertPactMagicStateEntity(
                    characterId = character.value.id,
                    classId = it.value.id,
                    slotsCurrentAmount = it1.currentAmount
                )
            }
        }
    }

    fun shortRest() {
        //TODO
    }

    private fun setName(it: String) {
        repository.changeName(it, character.value.id)
    }

    private fun setPersonalityTraits(it: String) {
        repository.setPersonalityTraits(it, character.value!!.id)
    }

    private fun setIdeals(it: String) {
        repository.setIdeals(it, character.value!!.id)
    }

    private fun setBonds(it: String) {
        repository.setBonds(it, character.value!!.id)
    }

    private fun setFlaws(it: String) {
        repository.setFlaws(it, character.value!!.id)
    }

    private fun setNotes(it: String) {
        repository.setNotes(it, character.value!!.id)
    }

    suspend fun infuse(targetItem: ItemInterface?, infusion: Infusion) {
        character.value?.let {
            if(activateInfusion(infusion, it)) {
                if (targetItem != null) {
                    it.backpack.applyInfusion(targetItem, infusion)
                }
                repository.insertCharacter(it)
            }
        }
    }

    suspend fun disableInfusion(infusion: Infusion) {
        character.value?.let {
            if(deactivateInfusion(infusion, it)) {
                it.backpack.removeInfusion(infusion)
                repository.insertCharacter(it)
            }
        }
    }

    private fun activateInfusion(infusion: Infusion, character: Character) : Boolean {
        character.classes.values.forEachIndexed { classIndex, clazz  ->
            clazz.levelPath!!.forEachIndexed { index, it ->
                if (it.grantsInfusions) {
                    if(character.classes.values.elementAt(classIndex).levelPath!![index].activateInfusion(infusion)) {
                        repository.activateInfusion(infusion.id, character.id)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun deactivateInfusion(infusion: Infusion, character: Character) : Boolean {
        character.classes.values.forEachIndexed { classIndex, clazz  ->
            clazz.levelPath!!.forEachIndexed { index, it ->
                if (it.grantsInfusions) {
                    if(character.classes.values.elementAt(classIndex).levelPath!![index].deactivateInfusion(infusion)) {
                        repository.deactivateInfusion(infusion.id, character.id)
                        return true
                    }
                }
            }
        }
        return false
    }
}