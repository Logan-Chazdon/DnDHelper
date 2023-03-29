package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.MutableLiveData
import gmail.loganchazdon.dndhelper.model.Language
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import javax.inject.Inject

class ProficiencyRepository @Inject constructor(
    LocalDataSource: LocalDataSource
) {
    private val _languages = LocalDataSource.getLanguages(
        MutableLiveData()
    )
    private val _skills =
        LocalDataSource.getAbilitiesToSkills(
            MutableLiveData()
        )

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

    fun getAllSkills(): MutableLiveData<Map<String, List<String>>> {
        return _skills
    }
}