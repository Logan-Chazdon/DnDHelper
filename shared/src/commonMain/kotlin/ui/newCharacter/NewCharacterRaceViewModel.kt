package ui.newCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.Race
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NewCharacterRaceViewModel constructor(
    private val raceRepository: RaceRepository,
    savedStateHandle: SavedStateHandle,
    val id: MutableStateFlow<Int>
) : ViewModel() {
    lateinit var races: Flow<List<Race>>

    init {
        viewModelScope.launch {
            races = raceRepository.getRaces()
        }
    }
}

