package model.localDataSources

import kotlinx.coroutines.flow.Flow
import model.*

expect interface DataSource {
    fun getItems(): Flow<List<ItemInterface>>
    fun getAbilitiesToSkills(): Flow<Map<String, List<String>>>
    fun getLanguages(): Flow<List<Language>>
    fun getMetaMagics(): Flow<List<Metamagic>>
    fun getArmors(): Flow<List<Armor>>
    fun getMiscItems(): Flow<List<ItemInterface>>
    fun getMartialWeapons(): Flow<List<Weapon>>
    fun getInfusions(): Flow<List<Infusion>>
    fun getSimpleWeapons(): Flow<List<Weapon>>
    fun getInvocations(): Flow<List<Feature>>
}