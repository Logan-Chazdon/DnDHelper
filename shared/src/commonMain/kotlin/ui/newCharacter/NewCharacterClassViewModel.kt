package ui.newCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import model.Character
import model.repositories.CharacterRepository
import model.repositories.ClassRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class NewCharacterClassViewModel constructor(
    private val characterRepository: CharacterRepository,
    classRepository: ClassRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var classes = classRepository.getClasses()
    var id = -1
    val character =  MutableStateFlow(Character())


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if (id != -1) {
            characterRepository.getLiveCharacterById(
                id,
                character
            )
        }
    }

    fun removeClass(classId: Int) {
        characterRepository.removeClassFromCharacter(classId = classId, characterId = id)
    }
}



