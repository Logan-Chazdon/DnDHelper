package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.*
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.*
import androidx.test.espresso.intent.Intents.init
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.inject.Inject
import com.example.dndhelper.ui.newCharacter.utils.indexOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import kotlin.math.max

@HiltViewModel
public class NewCharacterClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application){
    lateinit var classes : LiveData<List<Class>>
    var id = -1
    var isBaseClass =  mutableStateOf(true)
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownState>()


    init {
        viewModelScope.launch {
            classes = repository.getClasses()
        }

    }


    suspend fun addClassLevels(newClass: Class, level: Int) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        newClass.level = level
        //TODO add a check for more than one base class
        if(isBaseClass.value) {
            newClass.isBaseClass = isBaseClass.value
            newClass.equipmentChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Item>
            }
            newClass.proficiencyChoices.forEach {
                it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Proficiency>
            }
        }

        newClass.levelPath.filter { it.level <= level }.forEach {
            it.chosen = it.options?.let { it1 -> dropDownStates[it.name]?.getSelected(it1) } as List<Feature>
        }


        character!!.classes.add(newClass)
        repository.insertCharacter(character)
    }

}

