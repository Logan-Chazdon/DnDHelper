package model.localDataSources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.*

actual interface DataSource {
    actual fun getItems(): Flow<List<ItemInterface>>
    actual fun getAbilitiesToSkills(): Flow<Map<String, List<String>>>
    actual fun getLanguages(): Flow<List<Language>>
    actual fun getMetaMagics(): Flow<List<Metamagic>>
    actual fun getArmors(): Flow<List<Armor>>
    actual fun getMiscItems(): Flow<List<ItemInterface>>
    actual fun getMartialWeapons(): Flow<List<Weapon>>
    actual fun getInfusions(): Flow<List<Infusion>>
    actual fun getSimpleWeapons(): Flow<List<Weapon>>
    actual fun getInvocations(): Flow<List<Feature>>

}

class WebDataSourceImpl : DataSource {
    override fun getItems(): Flow<List<ItemInterface>> {
        return flow {

        }
    }

    override fun getAbilitiesToSkills(): Flow<Map<String, List<String>>> {
        return flow {

        }
    }

    override fun getLanguages(): Flow<List<Language>> {
        return flow {

        }
    }

    override fun getMetaMagics(): Flow<List<Metamagic>> {
        return flow {

        }
    }

    override fun getArmors(): Flow<List<Armor>> {
        return flow {

        }
    }

    override fun getMiscItems(): Flow<List<ItemInterface>> {
        return flow {

        }
    }

    override fun getMartialWeapons(): Flow<List<Weapon>> {
        return flow {

        }
    }

    override fun getInfusions(): Flow<List<Infusion>> {
        return flow {

        }
    }

    override fun getSimpleWeapons(): Flow<List<Weapon>> {
        return flow {

        }
    }

    override fun getInvocations(): Flow<List<Feature>> {
        return flow {

        }
    }
}