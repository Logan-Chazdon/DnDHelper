package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.SubclassEntity
import org.junit.Test

class SubclassServiceIntegrationTest {
    private val userOneService = SubclassService(client1)
    private val userTwoService = SubclassService(client2)


    private val users = listOf(
        userOneService to listOf(
            SubclassEntity(
                name = "UserOne Homebrew",
                spellAreFree = false,
                spellCasting = null,
                isHomebrew = true,
            ).apply {
                subclassId = 100
            },
            SubclassEntity(
                name = "UserOne Homebrew",
                spellAreFree = false,
                spellCasting = null,
                isHomebrew = true,
            ).apply {
                subclassId = 101
            }
        ),
        userTwoService to listOf(
            SubclassEntity(
                name = "UserTwo Homebrew",
                spellAreFree = false,
                spellCasting = null,
                isHomebrew = true,
            ).apply {
                subclassId = 100
            },
            SubclassEntity(
                name = "UserTwo Homebrew",
                spellAreFree = false,
                spellCasting = null,
                isHomebrew = true,
            ).apply {
                subclassId = 101
            }
        ),
    )

    @Test
    fun getSubclassesByClassId() {
    }

    @Test
    fun insertAndGetSubclass() = runTest {
        users.forEach {user ->
            user.second.forEach { subclass ->
                user.first.insertSubclass(subclass)
                val serverSubclass = user.first.getSubclass(subclass.subclassId).first()
                assert(subclass.name == serverSubclass.name)
            }
        }
    }


    @Test
    fun removeSubclassFeatureCrossRef() {
    }

    @Test
    fun insertSubclassFeatureCrossRef() {
    }

    @Test
    fun getHomebrewSubclasses() = runTest {
        users.forEach {user ->
            user.second.forEach { subclass ->
                user.first.insertSubclass(subclass)
            }

            val subclasses = user.first.getHomebrewSubclasses().first()
            assert(subclasses.size == 2)
        }
    }

    @Test
    fun getSubclassFeatures() {
    }

    @Test
    fun getSubclassLiveFeaturesById() {
    }

    @Test
    fun deleteSubclass() = runTest {
        users.forEach {user ->
            user.second.forEach { subclass ->
                user.first.insertSubclass(subclass)
            }

            user.second.forEach { subclass ->
                user.first.deleteSubclass(subclass.subclassId)
            }

            val subclasses = user.first.getHomebrewSubclasses().first()
            assert(subclasses.isEmpty())
        }
    }
}