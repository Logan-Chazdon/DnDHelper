package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.FeatureEntity
import org.junit.Test


class FeatureServiceIntegrationTest {

    private val userOneService = FeatureService(client1)
    private val userTwoService = FeatureService(client2)


    private val users = listOf(
        userOneService to listOf(
            FeatureEntity(
                name = "UserOne Homebrew 1",
                description = "",
                featureId = 100
            ),
            FeatureEntity(
                name = "UserOne Homebrew 2",
                description = "",
                featureId = 101
            )
        ),
        userTwoService to listOf(
            FeatureEntity(
                name = "UserTwo Homebrew 1",
                description = "",
                featureId = 100
            ),
            FeatureEntity(
                name = "UserTwo Homebrew 2",
                description = "",
                featureId = 101
            )
        ),
    )

    @Test
    fun fillOutFeatureListWithoutChosen() {
    }

    @Test
    fun insertAndGetFeature() = runTest {
        users.forEach { user->
            user.second.forEach { feature ->
                user.first.insertFeature(feature)

                val serverFeature = user.first.getLiveFeatureById(feature.featureId)
                assert(serverFeature.first().name == feature.name)
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