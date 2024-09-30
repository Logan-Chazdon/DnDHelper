package ui.newCharacter


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.Background
import model.Character
import model.repositories.BackgroundRepository
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
public class NewCharacterBackgroundViewModel constructor(
    private val backgroundRepository: BackgroundRepository,
    characterRepository: CharacterRepository,
     savedStateHandle: SavedStateHandle
): ViewModel(){
    lateinit var backgrounds : Flow<List<Background>>
    var id = -1
    val character: MutableStateFlow<Character> = MutableStateFlow(Character())

    init {
        viewModelScope.launch {
           backgrounds = backgroundRepository.getBackgrounds()
        }
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if(id !=-1) {
            characterRepository.getLiveCharacterById(
                savedStateHandle.get<String>("characterId")!!.toInt(),
                character
            )
        }
    }
}

