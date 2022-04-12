package com.example.dndhelper.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.repository.dataClasses.*
import com.example.dndhelper.repository.localDataSources.LocalDataSource
import com.example.dndhelper.repository.model.DatabaseDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class Repository @Inject constructor(
    LocalDataSource: LocalDataSource,
    private val dao: DatabaseDao?
) {
    private val _classes =
        LocalDataSource.getClasses(MutableLiveData())
    private val _races =
        LocalDataSource.getRaces(MutableLiveData())
    private val _backgrounds =
        LocalDataSource.getBackgrounds(
            MutableLiveData()
        )
    private val _languages = LocalDataSource.getLanguages(
        MutableLiveData()
    )
    private val _skills =
        LocalDataSource.getAbilitiesToSkills(
            MutableLiveData()
        )
    private val _items = LocalDataSource.getItems(
        MutableLiveData()
    )
    private val _feats = LocalDataSource.getFeats(MutableLiveData())
    private val _spells = LocalDataSource.getSpells(MutableLiveData())

    fun getClassIndex(name: String): Int {
        _classes.value?.forEachIndexed { index, it ->
            if (it.name == name) {
                return index
            }
        }
        return -1
    }

    fun getLanguages(): LiveData<List<Language>> {
        return _languages
    }

    fun getSkillsByIndex(index: String): MutableLiveData<Map<String, List<String>>>? {
        if (index == "skill_proficiencies") {
            return _skills
        }
        return null
    }

    fun getLanguagesByIndex(index: String): MutableLiveData<List<Language>>? {
        if (index == "all_languages") {
            return _languages
        }
        return null
    }

    fun getBackgrounds(): LiveData<List<Background>> {
        return _backgrounds
    }

    fun getRaces(): LiveData<List<Race>> {
        return _races
    }

    fun getFeats(): LiveData<List<Feat>> {
        return _feats
    }

    fun getClasses(): LiveData<List<Class>> {
        return _classes
    }

    fun getAllCharacters(): LiveData<List<Character>>? {
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

    suspend fun getCharacterById(id: Int): Character? {
        return dao?.findCharacterById(id)
    }

    fun getLiveCharacterById(id: Int): LiveData<Character>? {
        return dao?.findLiveCharacterById(id)
    }

    //Inserts a new character into the database and returns its ID
    fun createDefaultCharacter(): Int? {
        val newCharacter = Character(name = "My Character")
        return dao?.insertCharacter(newCharacter)?.toInt()
    }

    fun getAllItems(): LiveData<List<ItemInterface>> {
        return _items
    }

    fun getAllSpellsByClassIndex(classIndex: Int): MutableList<Spell> {
        val result = mutableListOf<Spell>()
        _spells.value?.forEach { spell ->
            if (_classes.value?.get(classIndex)
                    ?.let { it1 -> spell.classes.contains(it1.name.lowercase()) } == true
            ) {
                result.add(spell)
            }
        }
        return result
    }

    fun getAllSpells(): List<Spell> {
        return _spells.value ?: listOf()
    }

    companion object {
        val allSpellLevels = listOf(
            Pair(1, "First Level"),
            Pair(2, "Second Level"),
            Pair(3, "Third Level"),
            Pair(4, "Forth Level"),
            Pair(5, "Fifth Level"),
            Pair(6, "Sixth Level"),
            Pair(7, "Seventh Level"),
            Pair(8, "Eighth Level"),
            Pair(9, "Ninth Level"),
        )
    }
}