package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.BackgroundEntity
import model.FeatureEntity
import org.junit.Test

class BackgroundServiceIntegrationTest {
    private data class User(
        val backgrounds: List<BackgroundData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class BackgroundData(
        val entity: BackgroundEntity,
        val features: List<FeatureEntity> = emptyList()
    )


    private val users = listOf(
        User(
            client = client1,
            backgrounds = listOf(
                BackgroundData(
                    BackgroundEntity(
                        name = "UserOne Homebrew",
                    ).apply {
                        id = 100
                    }),
                BackgroundData(
                    BackgroundEntity(
                        "UserOne Homebrew2"
                    ).apply {
                        id = 101
                    })
            )
        ),
        User(
            client = client2,
            backgrounds = listOf(
                BackgroundData(
                    BackgroundEntity(
                        name = "UserTwo Homebrew",
                    ).apply {
                        id = 100
                    }),
                BackgroundData(
                    BackgroundEntity(
                        "UserTwo Homebrew2"
                    ).apply {
                        id = 101
                    })
            )
        ),
    )

    @Test
    fun getAllBackgrounds() = runTest {
        users.forEach { user ->
            val backgrounds = user.backgroundService.getAllBackgrounds().first()
            assert(backgrounds.isNotEmpty())
        }
    }

    @Test
    fun insertBackground() = runTest {
        users.forEach { user ->
            user.backgrounds.forEach { backgroundEntity ->
                user.backgroundService.insertBackground(backgroundEntity.entity)
            }

            val backgrounds = user.backgroundService.getAllBackgrounds().first()

            user.backgrounds.forEach { backgroundEntity ->
                assert(backgrounds.find { item -> item.name == backgroundEntity.entity.name } != null)
            }
        }
    }

    @Test
    fun insertBackgroundSpellCrossRef() {
    }

    @Test
    fun getBackgroundSpells() {
    }

    @Test
    fun deleteBackground() {
    }

    @Test
    fun getBackgroundFeatures() {
    }

    @Test
    fun getUnfilledBackgroundFeatures() {
    }

    @Test
    fun getHomebrewBackgrounds() = runTest {
        users.forEach { user ->
            val items = user.backgroundService.getHomebrewBackgrounds().first()
            assert(items.isNotEmpty())
        }
    }

    @Test
    fun getBackgroundChoiceData() {
    }

    @Test
    fun getUnfilledBackground() = runTest {
        users.forEach { user ->
            user.backgrounds.forEach {
                user.backgroundService.insertBackground(it.entity)

                val background = user.backgroundService.getUnfilledBackground(it.entity.id).first()
            }
        }
    }

    @Test
    fun insertBackgroundFeatureCrossRef() = runTest {

    }
}