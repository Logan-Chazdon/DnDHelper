package ui.newCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.Character
import model.repositories.CharacterRepository
import model.repositories.ClassRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class NewCharacterClassViewModel constructor(
    private val characterRepository: CharacterRepository,
    classRepository: ClassRepository,
    savedStateHandle: SavedStateHandle,
    val id : MutableStateFlow<Int>
) : ViewModel() {
    var classes = classRepository.getClasses()
    val character =  MutableStateFlow(Character())


    init {
        viewModelScope.launch {
            id.collect {
                characterRepository.getLiveCharacterById(
                    it,
                    character
                )
            }
        }
    }

    suspend fun removeClass(classId: Int) {
        characterRepository.removeClassFromCharacter(classId = classId, characterId = id.value)
    }
}



