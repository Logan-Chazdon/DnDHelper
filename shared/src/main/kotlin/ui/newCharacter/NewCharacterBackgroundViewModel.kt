package ui.newCharacter

import android.app.Application
import androidx.lifecycle.*
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
    application: Application, savedStateHandle: SavedStateHandle
): AndroidViewModel(application){
    lateinit var backgrounds : LiveData<List<Background>>
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()

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

