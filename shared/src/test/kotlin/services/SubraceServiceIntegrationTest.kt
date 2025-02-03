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
                    },
                    listOf(
                        FeatureEntity(
                            name = "",
                            description = "",
                            featureId = 3854
                        )
                    )),
                SubraceData(
                    SubraceEntity(
                        name = "UserOne Homebrew 2",
                        isHomebrew = true,
                    ).apply {
                        id = 101
                    },
                    listOf(
                        FeatureEntity(
                            name = "",
                            description = "",
                            featureId = 3844
                        ),
                        FeatureEntity(
                            name = "",
                            description = "",
                            featureId = 3845
                        ),
                        FeatureEntity(
                            name = "",
                            description = "",
                            featureId = 3846
                        ),
                    )
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
    fun testFeatureCrossRef() = runTest {
        users.forEach { user ->
            user.subraces.forEach { subrace ->
                user.subraceService.insertSubrace(subrace.entity)

                subrace.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.subraceService.insertSubraceFeatureCrossRef(
                        subraceId = subrace.entity.id,
                        featureId = feature.featureId
                    )
                }

                val features = user.subraceService.getSubraceLiveFeaturesById(subrace.entity.id)

                assert(features.first().map { it.featureId} == subrace.features.map { it.featureId} )

                subrace.features.forEach { feature ->
                    user.subraceService.removeSubraceFeatureCrossRef(
                        subraceId = subrace.entity.id,
                        featureId = feature.featureId
                    )
                }

                assert(features.first().isEmpty())

            }
        }
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
    fun bindSubraceOptions() {
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