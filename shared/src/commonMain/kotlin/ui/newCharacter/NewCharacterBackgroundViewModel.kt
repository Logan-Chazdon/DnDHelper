package ui.newCharacter


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
    val id: MutableStateFlow<Int>
) : ViewModel() {
    lateinit var backgrounds: Flow<List<Background>>
    val character: MutableStateFlow<Character> = MutableStateFlow(Character())

    init {
        viewModelScope.launch {
            backgrounds = backgroundRepository.getBackgrounds()
        }

        viewModelScope.launch {
            id.collect {
                characterRepository.getLiveCharacterById(
                    it,
                    character
                )
            }
        }
    }
}

