package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.RaceEntity
import org.junit.Test

class RaceServiceIntegrationTest {

    private data class User(
        val races: List<RaceData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class RaceData(
        val entity: RaceEntity,
        val races: List<RaceEntity> = emptyList()
    )

    private val users = listOf(
        User(
            client = client1,
            races= listOf(
                RaceData(
                    RaceEntity(
                        raceName = "userOneHomebrew",
                        raceId = 200
                    )
                )
            )
        ),
        User(
            client = client2,
            races= listOf(
                RaceData(
                    RaceEntity(
                        raceName = "userTwoHomebrew",
                        raceId = 200
                    )
                )
            )
        )
    )


    @Test
    fun insertRace() = runTest {
        users.forEach { user ->
            user.races.forEach {
                user.raceService.insertRace(it.entity)
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
            user.races.forEach {
                user.raceService.insertRace(it.entity)
            }

            val races = user.raceService.getAllRaces().first()
            assert(races.isNotEmpty())
        }
    }

    @Test
    fun deleteRace() = runTest {
        users.forEach { user ->
            user.races.forEach {
                user.raceService.insertRace(it.entity)
            }

            user.races.forEach {
                user.raceService.deleteRace(it.entity.raceId)
            }
            val races = user.raceService.getHomebrewRaces().first()
            assert(races.isEmpty())
        }
    }

    @Test
    fun getHomebrewRaces() = runTest {
        users.forEach { user ->
            user.races.forEach {
                user.raceService.insertRace(it.entity)
            }

            val races = user.raceService.getHomebrewRaces().first()
            assert(races.size == user.races.size)
        }
    }

    @Test
    fun findUnfilledLiveRaceById() = runTest {
        users.forEach { user ->
            user.races.forEach {
                user.raceService.insertRace(it.entity)
            }

            user.races.forEach {
                val serverRace = user.raceService.findUnfilledLiveRaceById(it.entity.raceId)
                assert(serverRace.first().raceName == it.entity.raceName)
            }
        }
    }

    @Test
    fun getRaceSubraces() {
    }

    @Test
    fun getAllRaceIdsAndNames() = runTest {
        users.forEach { user ->
            user.races.forEach {
                user.raceService.insertRace(it.entity)
            }

            val races = user.raceService.getAllRaceIdsAndNames().first()
            assert(races.size == user.races.size)
        }
    }
}