package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Background
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Language
import com.example.dndhelper.repository.dataClasses.LanguageChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max


@HiltViewModel
public class NewCharacterBackgroundViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application){
    lateinit var backgrounds : LiveData<List<Background>>
    var backgroundIndex : Int = -1
    var id = -1
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()

    init {
        viewModelScope.launch {
           backgrounds = repository.getBackgrounds()
        }

    }


    fun getLanguagesByIndex(index: String) : LiveData<List<Language>> {
        return repository.getLanguages()
    }

    suspend fun setBackGround(newBackground : Background) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        character!!.background = newBackground
        repository.insertCharacter(character)
    }

    fun getLanguageChoice(choice: LanguageChoice): List<Language> {
        val langs = mutableListOf<Language>()

        choice.from.forEach {
             if(it.index != null) {
                 val newLangs = repository.getLanguagesByIndex(it.index!!)
                 newLangs?.value?.let { it1 -> langs.addAll(it1) }
             }
             if(it.name != null) {
                 langs.add(
                     it
                 )
             }
        }

        return langs
    }



}

