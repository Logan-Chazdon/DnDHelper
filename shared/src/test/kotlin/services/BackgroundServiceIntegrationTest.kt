package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.BackgroundEntity
import org.junit.Test

class BackgroundServiceIntegrationTest {
    private val userOneService = BackgroundService(client1)
    private val userTwoService = BackgroundService(client2)


    private val users = listOf(
        userOneService to listOf(
            BackgroundEntity(
                name  ="UserOne Homebrew",
            ).apply {
                id = 100
            },
            BackgroundEntity(
                "UserOne Homebrew2"
            ).apply {
                id = 101
            }
        ),
        userTwoService to listOf(
            BackgroundEntity(
                "UserTwo Homebrew"
            ).apply {
                id =100
            },
            BackgroundEntity(
                "UserTwo Homebrew2"
            ).apply {
                id = 101
            }
        ),
    )

    @Test
    fun getAllBackgrounds() = runTest {
        users.forEach { user ->
            val backgrounds = user.first.getAllBackgrounds().first()
            assert(backgrounds.isNotEmpty())
        }
    }

    @Test
    fun insertBackground() = runTest {
        users.forEach { user ->
            user.second.forEach { backgroundEntity ->
                user.first.insertBackground(backgroundEntity)
            }

            val backgrounds = user.first.getAllBackgrounds().first()

            user.second.forEach { backgroundEntity ->
                 assert(backgrounds.find { item -> item.name == backgroundEntity.name } != null)
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
            val items = user.first.getHomebrewBackgrounds().first()
            assert(items.isNotEmpty())
        }
    }

    @Test
    fun getBackgroundChoiceData() {
    }

    @Test
    fun getUnfilledBackground() = runTest {
        users.forEach {user ->
            user.second.forEach {
                user.first.insertBackground(it)

                val background = user.first.getUnfilledBackground(it.id).first()
            }
        }
    }

    @Test
    fun insertBackgroundFeatureCrossRef() = runTest {

    }
}