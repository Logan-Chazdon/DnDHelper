package model.localDataSources

import io.ktor.client.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import model.*
import services.Service

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

private const val PATH = "datasource"

class WebDataSourceImpl(client: HttpClient) : DataSource, Service(client = client) {
    sealed class Path <T> (
        private val subpath: String,
        val cache: MutableStateFlow<T>,
        var isCalculated: Boolean = false,
    ) {
        data object MartialWeapons : Path<List<Weapon>>("martialWeapons", MutableStateFlow(emptyList()))
        data object SimpleWeapons : Path<List<Weapon>>("simpleWeapons", MutableStateFlow(emptyList()))
        data object Armors : Path<List<Armor>>("armors", MutableStateFlow(emptyList()))
        data object MiscItems : Path<List<Item>>("miscItems", MutableStateFlow(emptyList()))


        data object Invocations : Path<List<Feature>>("invocations", MutableStateFlow(emptyList()))
        data object Infusions : Path<List<Infusion>>("infusions", MutableStateFlow(emptyList()))
        data object Languages : Path<List<Language>>("languages", MutableStateFlow(emptyList()))
        data object Metamagics : Path<List<Metamagic>>("metamagics", MutableStateFlow(emptyList()))
        data object AbilitiesToSkills : Path<Map<String, List<String>>>("abilitiesToSkills", MutableStateFlow(emptyMap()))

        val path
            get() = "$PATH/$subpath"
    }

    private inline fun <reified T> cachedGet(
        path: Path<T>,
        crossinline deserialize: (String) -> T = {format.decodeFromString(it)}
    ): MutableStateFlow<T> {
        client.launch {
            if (!path.isCalculated) {
                //Get json from server
                val str = getFrom(path.path) {}.bodyAsText()

                //Emit desearilzied.
                path.cache.emit(deserialize(str))
            }
        }
        return path.cache
    }



    override fun getItems(): Flow<List<ItemInterface>> {
        cachedGet(Path.Armors)
        cachedGet(Path.SimpleWeapons)
        cachedGet(Path.MartialWeapons)
        cachedGet(Path.MiscItems)

        return combine(
            Path.Armors.cache,
            Path.SimpleWeapons.cache,
            Path.MartialWeapons.cache,
            Path.MiscItems.cache,
        ) { armors, weapons, weapons2, items ->
            val result = mutableListOf<ItemInterface>()
            result.addAll(armors)
            result.addAll(weapons)
            result.addAll(weapons2)
            result.addAll(items)

            result
        }
    }

    override fun getAbilitiesToSkills(): Flow<Map<String, List<String>>> {
        return cachedGet(Path.AbilitiesToSkills) {
            format.decodeFromString<List<Pair<String, List<String>>>>(it).toMap()
        }
    }

    override fun getLanguages(): Flow<List<Language>> {
        return cachedGet(Path.Languages)
    }

    override fun getMetaMagics(): Flow<List<Metamagic>> {
        return cachedGet(Path.Metamagics)
    }

    override fun getArmors(): Flow<List<Armor>> {
        return cachedGet(Path.Armors)
    }

    override fun getMiscItems(): Flow<List<ItemInterface>> {
        return cachedGet(Path.MiscItems)
    }

    override fun getMartialWeapons(): Flow<List<Weapon>> {
        return cachedGet(Path.MartialWeapons)
    }

    override fun getInfusions(): Flow<List<Infusion>> {
        return cachedGet(Path.Infusions)
    }

    override fun getSimpleWeapons(): Flow<List<Weapon>> {
        return cachedGet(Path.SimpleWeapons)
    }

    override fun getInvocations(): Flow<List<Feature>> {
        return cachedGet(Path.Invocations)
    }
}