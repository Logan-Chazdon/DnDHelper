package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.FeatureEntity
import model.RaceEntity
import model.SubraceEntity
import org.junit.Test

class RaceServiceIntegrationTest {

    private data class User(
        val races: List<RaceData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class RaceData(
        val entity: RaceEntity,
        val subraces: List<SubraceEntity> = emptyList(),
        val features: List<FeatureEntity> = emptyList()
    )

    private val users = listOf(
        User(
            client = client1,
            races = listOf(
                RaceData(
                    RaceEntity(
                        raceName = "userOneHomebrew",
                        raceId = 200
                    ),
                    subraces = listOf(
                        SubraceEntity().apply {
                            id = 20000
                        }
                    ),
                    features = listOf(
                        FeatureEntity(
                            name = "Test",
                            description = "",
                            featureId = 40000
                        )
                    )
                )
            )
        ),
        User(
            client = client2,
            races = listOf(
                RaceData(
                    RaceEntity(
                        raceName = "userTwoHomebrew",
                        raceId = 200
                    ),
                    subraces = listOf(
                        SubraceEntity().apply {
                            id = 20000
                        }
                    ),
                    features = listOf(
                        FeatureEntity(
                            name = "Test",
                            description = "",
                            featureId = 40000
                        )
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
    fun insertAndRemoveRaceFeatureCrossRef() = runTest {
        users.forEach { user ->
            user.races.forEach { entity ->
                user.raceService.insertRace(entity.entity)

                entity.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.raceService.insertRaceFeatureCrossRef(
                        featureId = feature.featureId,
                        raceId = entity.entity.raceId
                    )
                }

                val features = user.raceService.getRaceFeatures(entity.entity.raceId)

                assert(features.map { it.featureId } == entity.features.map { it.featureId })


                entity.features.forEach { feature ->
                    user.raceService.removeRaceFeatureCrossRef(
                        featureId = feature.featureId,
                        raceId = entity.entity.raceId
                    )
                }

                assert(user.raceService.getRaceFeatures(entity.entity.raceId).isEmpty())
            }
        }
    }


    @Test
    fun insertRaceSubraceCrossRef() = runTest {
        users.forEach { user ->
            user.races.forEach { entity ->
                user.raceService.insertRace(entity.entity)

                entity.subraces.forEach { subrace ->
                    user.subraceService.insertSubrace(subrace)

                    user.raceService.insertRaceSubraceCrossRef(
                        subraceId = subrace.id,
                        raceId = entity.entity.raceId
                    )
                }

                val subraces = user.raceService.getRaceSubraces(entity.entity.raceId)

                assert(subraces.first().map { it.id } == entity.subraces.map { it.id })
            }
        }
    }

    @Test
    fun getRaceFeatures() = runTest {
        users.forEach { user ->
            user.races.forEach { entity ->
                user.raceService.insertRace(entity.entity)

                entity.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.raceService.insertRaceFeatureCrossRef(
                        featureId = feature.featureId,
                        raceId = entity.entity.raceId
                    )
                }

                val features = user.raceService.getRaceFeatures(entity.entity.raceId)

                assert(features.map { it.featureId } == entity.features.map { it.featureId })
            }
        }
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