package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Background
import com.example.dndhelper.repository.dataClasses.Item
import com.example.dndhelper.repository.dataClasses.Language
import com.example.dndhelper.repository.dataClasses.LanguageChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


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


    suspend fun setBackGround(newBackground : Background) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        newBackground.languageChoices.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Language>
        }
        newBackground.equipmentChoices.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
        }
        character!!.setNewBackground(newBackground)
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

