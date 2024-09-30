package model.repositories


import kotlinx.coroutines.flow.Flow
import model.Language
import model.localDataSources.DataSource


 class ProficiencyRepository {
     constructor(dataSource: DataSource) {
        this._languages = dataSource.getLanguages()
        this._skills = dataSource.getAbilitiesToSkills()
    }

    private val _languages: Flow<List<Language>>
    private val _skills: Flow<Map<String, List<String>>>

     fun getSkillsByIndex(index: String): Flow<Map<String, List<String>>>? {
        if (index == "skill_proficiencies") {
            return _skills
        }
        return null
    }

     fun getLanguagesByIndex(index: String): Flow<List<Language>>? {
        if (index == "all_languages") {
            return _languages
        }
        return null
    }

     fun getAllSkills(): Flow<Map<String, List<String>>> {
        return _skills
    }
}