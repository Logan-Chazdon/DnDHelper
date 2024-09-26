package model.repositories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import model.Language
import model.localDataSources.LocalDataSource


actual class ProficiencyRepository constructor(
    localDataSource: LocalDataSource
) {
    private val _languages = localDataSource.getLanguages(
        MutableLiveData()
    )
    private val _skills =
        localDataSource.getAbilitiesToSkills(
            MutableLiveData()
        )

    actual fun getSkillsByIndex(index: String): Flow<Map<String, List<String>>>? {
        if (index == "skill_proficiencies") {
            return _skills.asFlow()
        }
        return null
    }

    actual fun getLanguagesByIndex(index: String): Flow<List<Language>>? {
        if (index == "all_languages") {
            return _languages.asFlow()
        }
        return null
    }

    actual fun getAllSkills(): Flow<Map<String, List<String>>> {
        return _skills.asFlow()
    }
}