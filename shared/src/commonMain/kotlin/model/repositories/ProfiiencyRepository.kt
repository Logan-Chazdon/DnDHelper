package model.repositories

import kotlinx.coroutines.flow.Flow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class ProficiencyRepository {
    fun getSkillsByIndex(index: String): Flow<Map<String, List<String>>>?
    fun getLanguagesByIndex(index: String): Flow<List<model.Language>>?
    fun getAllSkills(): Flow<Map<String, List<String>>>
}