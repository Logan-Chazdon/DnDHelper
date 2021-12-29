package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
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
    val selectedList =  mutableStateListOf<Boolean>()
    val selectedNames by lazy {
        MutableLiveData<String>("")
    }

    init {
        viewModelScope.launch {
           backgrounds = repository.getBackgrounds()
        }

    }

    fun changeSelection(index:Int, maxSelections: Int) {
        val selections = selectedList.count { Boolean -> Boolean }
        if(selections >= maxSelections && !selectedList[index]) {
            selectedList[selectedList.indexOf(true)] = false
        }
        selectedList[index] = !selectedList[index]

        //Update the name to only show the selected options.
        selectedNames.value = ""
        for(i in backgrounds.value?.get(backgroundIndex)?.languageChoices?.get(0)?.from?.indices!!) {
            if(selectedList[i]) {
                selectedNames.value +=
                    backgrounds.value?.get(backgroundIndex)?.languageChoices?.get(0)?.from!![i].name
            }
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

    fun setLanguageChoiceLength(len : Int) {
        for(i in 0 until len) {
            if(i >= selectedList.size) {
                selectedList.add(i, false)
            }
        }
    }

}

