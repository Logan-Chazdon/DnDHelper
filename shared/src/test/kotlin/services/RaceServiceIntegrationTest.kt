package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.RaceEntity
import org.junit.Test

class RaceServiceIntegrationTest {
    private val userOneService = RaceService(client1)
    private val userTwoService = RaceService(client2)

    private val users = listOf(
        userOneService to listOf(
            RaceEntity(
                raceName = "userOneHomebrew",
                raceId = 200
            )
        ),
        userTwoService to listOf(
            RaceEntity(
                raceName = "userTwoHomebrew",
                raceId = 200
            )
        )
    )


    @Test
    fun insertRace() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }
        }
    }

    @Test
    fun insertRaceFeatureCrossRef() {
    }

    @Test
    fun removeRaceFeatureCrossRef() {
    }

    @Test
    fun insertRaceSubraceCrossRef() {
    }

    @Test
    fun getRaceFeatures() {
    }

    @Test
    fun getSubraceFeatures() {
    }

    @Test
    fun getSubraceFeatChoices() {
    }

    @Test
    fun getAllRaces() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }

            val races = user.first.getAllRaces().first()
            assert(races.isNotEmpty())
        }
    }

    @Test
    fun deleteRace() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }

            user.second.forEach {
                user.first.deleteRace(it.raceId)
            }
            val races = user.first.getHomebrewRaces().first()
            assert(races.isEmpty())
        }
    }

    @Test
    fun getHomebrewRaces() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }

            val races = user.first.getHomebrewRaces().first()
            assert(races.size == user.second.size)
        }
    }

    @Test
    fun findUnfilledLiveRaceById() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }

            user.second.forEach {
                val serverRace = user.first.findUnfilledLiveRaceById(it.raceId)
                assert(serverRace.first().raceName == it.raceName)
            }
        }
    }

    @Test
    fun getRaceSubraces() {
    }

    @Test
    fun getAllRaceIdsAndNames() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertRace(it)
            }

            val races = user.first.getAllRaceIdsAndNames().first()
            assert(races.size == user.second.size)
        }
    }
}