package ui.newCharacter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import model.Race
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NewCharacterRaceViewModel constructor(
    private val raceRepository: RaceRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val characterId: Int = savedStateHandle.get<String>("characterId")!!.toInt()
    lateinit var races: LiveData<List<Race>>

    init {
        viewModelScope.launch {
            races = raceRepository.getRaces()
        }
    }
}

