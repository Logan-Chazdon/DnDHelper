package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.BackgroundEntity
import model.FeatureEntity
import model.Spell
import org.junit.Test
import kotlin.test.assertFails

class BackgroundServiceIntegrationTest {
    private data class User(
        val backgrounds: List<BackgroundData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class BackgroundData(
        val entity: BackgroundEntity,
        val features: List<FeatureEntity> = emptyList(),
        val spells: List<Spell> = listOf(
            Spell(
                name = "Spell",
                level = 2,
                components = emptyList(),
                itemComponents = emptyList(),
                school = "",
                desc = "",
                range = "",
                area = "",
                castingTime = "",
                duration = "",
                classes = emptyList(),
                damage = "",
                isRitual = false,
                isHomebrew = true
            ).apply {
                id = 200
            }
        )
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
                    },
                    features = listOf(
                        FeatureEntity(
                            featureId = 20000,
                            name = "Test",
                            description = ""
                        ),
                        FeatureEntity(
                            featureId = 20001,
                            name = "Test",
                            description = ""
                        )
                    )
                ),
                BackgroundData(
                    BackgroundEntity(
                        "UserOne Homebrew2"
                    ).apply {
                        id = 101
                    },
                    features = listOf(
                        FeatureEntity(
                            featureId = 20002,
                            name = "Test",
                            description = ""
                        ),
                        FeatureEntity(
                            featureId = 20001,
                            name = "Test",
                            description = ""
                        )
                    )
                )
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
                    },
                    features = listOf(
                        FeatureEntity(
                            featureId = 20000,
                            name = "Test",
                            description = ""
                        ),
                        FeatureEntity(
                            featureId = 20001,
                            name = "Test",
                            description = ""
                        )
                    )
                ),
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
    fun getBackgroundSpells() = runTest {
        users.forEach { user ->
            user.backgrounds.forEach { background ->
                user.backgroundService.insertBackground(background.entity)

                background.spells.forEach { spell ->
                    user.spellService.insertSpell(spell)

                    user.backgroundService.insertBackgroundSpellCrossRef(
                        backgroundId = background.entity.id,
                        spellId = spell.id
                    )
                }

                val spells = user.backgroundService.getBackgroundSpells(background.entity.id)
                assert(spells!!.map { it.id } == background.spells.map { it.id })
            }
        }
    }

    @Test
    fun deleteBackground() = runTest {
        users.forEach { user ->
            user.backgrounds.forEach { background ->
                user.backgroundService.insertBackground(background.entity)

                user.backgroundService.deleteBackground(background.entity.id)

                assertFails {
                    user.backgroundService.getUnfilledBackground(background.entity.id).first()
                }
            }
        }
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
        users.forEach { user ->
            user.backgrounds.forEach { entity ->
                user.backgroundService.insertBackground(entity.entity)

                entity.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.backgroundService.insertBackgroundFeatureCrossRef(
                        backgroundId = entity.entity.id,
                        featureId = feature.featureId
                    )
                }

                val features = user.backgroundService.getBackgroundFeatures(entity.entity.id)

                assert(features.map { it.featureId } == entity.features.map { it.featureId})
            }
        }
    }
}