package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.SubraceEntity
import org.junit.Test

class SubraceServiceIntegrationTest {
    private val userOneService = SubraceService(client1)
    private val userTwoService = SubraceService(client2)


    private val users = listOf(
        userOneService to listOf(
            SubraceEntity(
                name = "UserOne Homebrew",
                isHomebrew = true,
            ).apply {
                id = 100
            },
            SubraceEntity(
                name = "UserOne Homebrew 2",
                isHomebrew = true,
            ).apply {
                id = 101
            }
        ),
        userTwoService to listOf(
            SubraceEntity(
                name = "UserTwo Homebrew",
                isHomebrew = true,
            ).apply {
                id = 100
            },
            SubraceEntity(
                name = "UserTwo Homebrew 2",
                isHomebrew = true,
            ).apply {
                id = 101
            }
        ),
    )


    @Test
    fun insertSubraceFeatureCrossRef() {
    }

    @Test
    fun insertAndGetSubrace() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertSubrace(it)
                val serverSubrace = user.first.getSubrace(it.id)
                assert(serverSubrace.first().name == it.name)
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
            user.second.forEach {
                user.first.insertSubrace(it)
            }

            val subraces = user.first.getHomebrewSubraces()
            assert(subraces.first().isNotEmpty())
        }
    }

    @Test
    fun getSubraceLiveFeaturesById() {
    }

    @Test
    fun deleteSubrace() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertSubrace(it)
            }

            user.second.forEach {
                user.first.deleteSubrace(it.id)
            }

            val subraces = user.first.getHomebrewSubraces()
            assert(subraces.first().isEmpty())
        }
    }
}