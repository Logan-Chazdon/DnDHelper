package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.FeatureEntity
import org.junit.Test


class FeatureServiceIntegrationTest {
    private data class User(
        val features: List<FeatureData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class FeatureData(
        val entity: FeatureEntity,
    )


    private val users = listOf(
        User(
            client = client1,
            features = listOf(
                FeatureData(
                    FeatureEntity(
                        name = "UserOne Homebrew 1",
                        description = "",
                        featureId = 100
                    )
                ),
                FeatureData(
                    FeatureEntity(
                        name = "UserOne Homebrew 2",
                        description = "",
                        featureId = 101
                    )
                )
            )
        ),
        User(
            client = client2,
            features = listOf(
                FeatureData(
                    FeatureEntity(
                        name = "UserTwo Homebrew 1",
                        description = "",
                        featureId = 100
                    )
                ),
                FeatureData(
                    FeatureEntity(
                        name = "UserTwo Homebrew 2",
                        description = "",
                        featureId = 101
                    )
                )
            )
        ),
    )

    @Test
    fun fillOutFeatureListWithoutChosen() {
    }

    @Test
    fun insertAndGetFeature() = runTest {
        users.forEach { user ->
            user.features.forEach { feature ->
                user.featureService.insertFeature(feature.entity)

                val serverFeature = user.featureService.getLiveFeatureById(feature.entity.featureId)
                assert(serverFeature.first().name == feature.entity.name)
            }
        }
    }

    @Test
    fun insertFeatureOptionsCrossRef() {
    }

    @Test
    fun insertFeatureChoice() {
    }

    @Test
    fun removeFeatureOptionsCrossRef() {
    }

    @Test
    fun removeOptionsFeatureCrossRef() {
    }

    @Test
    fun insertFeatureChoiceIndexCrossRef() {
    }

    @Test
    fun insertIndexRef() {
    }

    @Test
    fun removeIdFromRef() {
    }

    @Test
    fun insertFeatureSpellCrossRef() {
    }

    @Test
    fun removeFeatureSpellCrossRef() {
    }

    @Test
    fun insertOptionsFeatureCrossRef() {
    }

    @Test
    fun removeFeatureFeatureChoice() {
    }

    @Test
    fun getFeatureChoices() {
    }

    @Test
    fun getFeatureSpells() {
    }

    @Test
    fun getLiveFeatureChoices() {
    }

    @Test
    fun getFeatureChoiceOptions() {
    }

    @Test
    fun clearFeatureChoiceIndexRefs() {
    }

    @Test
    fun getFeatureIdOr0FromSpellId() {
    }

    @Test
    fun getLiveFeatureSpells() {
    }

    @Test
    fun returnGetAllIndexes() {
    }
}