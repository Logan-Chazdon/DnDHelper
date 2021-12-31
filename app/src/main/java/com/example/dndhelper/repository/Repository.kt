package com.example.dndhelper.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.repository.model.DatabaseDao
import com.example.dndhelper.repository.webServices.Webservice
import javax.inject.Inject
import com.example.dndhelper.repository.webServices.WebserviceDnD
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.dndhelper.repository.dataClasses.*


class Repository @Inject constructor(
    private var webservice: Webservice?,
    private val dao: DatabaseDao?,

) {
    private val _classes : MutableLiveData<List<Class>> = MutableLiveData<List<Class>>()
    private val _races : MutableLiveData<List<Race>> = MutableLiveData<List<Race>>()
    private val _backgrounds = MutableLiveData<List<Background>>()
    private val _characters : LiveData<List<Character>>? = dao?.getAllCharacters()
    private val _languages = MutableLiveData<List<Language>>()
    private val _skills : MutableLiveData<Map<String, List<String>>> =
        MutableLiveData()
    private val _items: MutableLiveData<List<Item>> = MutableLiveData()

    init {
        //Items
        (webservice as WebserviceDnD).getItems(_items)

        //backgrounds
        (webservice as WebserviceDnD).getLocalBackgrounds(_backgrounds)

        //languages
        (webservice as WebserviceDnD).getLocalLanguages(_languages)

        //Skills
        (webservice as WebserviceDnD).getSkills(_skills)

        //classes
        GlobalScope.launch {
            _classes.postValue(dao?.getAllClasses())
        }
        _classes.observeForever {
            GlobalScope.launch {
                dao?.insertClasses(it)
            }
        }
        (webservice as WebserviceDnD).getLocalClasses(_classes)

        //races
        GlobalScope.launch {
            _races.postValue(dao?.getAllRaces())
        }
        _races.observeForever {
            GlobalScope.launch {
                dao?.insertRaces(it)
            }
        }
        (webservice as WebserviceDnD).getLocalRaces(_races)

    }

    fun getLanguages() : LiveData<List<Language>> {
        return _languages
    }

    fun getSkillsByIndex(index: String):
            MutableLiveData<Map<String, List<String>>>? {
        if(index == "skill_proficiencies"){
            return _skills
        }
        return null
    }

    fun getLanguagesByIndex(index: String): MutableLiveData<List<Language>>? {
        if(index == "all_languages")
        {
            return _languages
        }
        return null
    }

    fun getBackgrounds() : LiveData<List<Background>> {
        return _backgrounds
    }

    fun getRaces(): LiveData<List<Race>> {
        return _races
    }

    fun getClasses(): LiveData<List<Class>> {
        return _classes
    }

    fun getAllCharacters() : LiveData<List<Character>>? {
        return dao?.getAllCharacters()
    }

    fun insertCharacter(character: Character) {
        dao?.insertCharacter(character)
    }

    fun deleteCharacterById(id: Int) {
        GlobalScope.launch {
            dao?.deleteCharacter(id)
        }
    }

    suspend fun getCharacterById(id: Int) : Character? {
        return dao?.findCharacterById(id)
    }

    //Inserts a new character into the database and returns its ID
    fun createDefaultCharacter() : Int? {
        val newCharacter = Character(name = "My Character")
        return dao?.insertCharacter(newCharacter)?.toInt()
    }


}