package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.FeatureEntity
import model.SubraceEntity
import org.junit.Test

class SubraceServiceIntegrationTest {

    private data class User(
        val subraces: List<SubraceData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class SubraceData(
        val entity: SubraceEntity,
        val features: List<FeatureEntity> = emptyList(),
    )

    private val users = listOf(
        User(
            client = client1,
            subraces = listOf(
                SubraceData(
                    SubraceEntity(
                        name = "UserOne Homebrew",
                        isHomebrew = true,
                    ).apply {
                        id = 100
                    }),
                SubraceData(
                    SubraceEntity(
                        name = "UserOne Homebrew 2",
                        isHomebrew = true,
                    ).apply {
                        id = 101
                    }
                ))),
        User(
            client = client2,
            subraces = listOf(
                SubraceData(
                    SubraceEntity(
                        name = "UserTwo Homebrew",
                        isHomebrew = true,
                    ).apply {
                        id = 100
                    }),
                SubraceData(
                    SubraceEntity(
                        name = "UserTwo Homebrew 2",
                        isHomebrew = true,
                    ).apply {
                        id = 101
                    }
                )))
    )


    @Test
    fun insertSubraceFeatureCrossRef() {
    }

    @Test
    fun insertAndGetSubrace() = runTest {
        users.forEach { user ->
            user.subraces.forEach {
                user.subraceService.insertSubrace(it.entity)
                val serverSubrace = user.subraceService.getSubrace(it.entity.id)
                assert(serverSubrace.first().name == it.entity.name)
            }
        }
    }

    @Test
    fun removeSubraceFeatureCrossRef() {
    }


    @Test
    fun bindSubraceOptions() {
    }

    @Test
    fun removeRaceSubraceCrossRef() {
    }

    @Test
    fun getHomebrewSubraces() = runTest {
        users.forEach { user ->
            user.subraces.forEach {
                user.subraceService.insertSubrace(it.entity)
            }

            val subraces = user.subraceService
                .getHomebrewSubraces()
            assert(subraces.first().isNotEmpty())
        }
    }

    @Test
    fun getSubraceLiveFeaturesById() {
    }

    @Test
    fun deleteSubrace() = runTest {
        users.forEach { user ->
            user.subraces.forEach {
                user.subraceService.insertSubrace(it.entity)
            }

            user.subraces.forEach {
                user.subraceService.deleteSubrace(it.entity.id)
            }

            val subraces = user.subraceService.getHomebrewSubraces()
            assert(subraces.first().isEmpty())
        }
    }
}