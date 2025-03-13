package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.Feature
import model.FeatureChoice
import model.IndexRef
import model.Spell
import org.junit.Test


class FeatureServiceIntegrationTest {
    private data class User(
        val features: List<FeatureData>,
        val indices: List<IndexRef> = emptyList(),
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class FeatureData(
        val entity: Feature,
        val options: List<FeatureChoice> = emptyList(),
        val spells: List<Spell> = emptyList(),
    )


    private val users = listOf(
        User(
            client = client1,
            features = listOf(
                FeatureData(
                    Feature(
                        name = "UserOne Homebrew 1",
                        description = "",
                        featureId = 100
                    ),
                    options = listOf(
                        FeatureChoice().apply {
                            this.id = 1000
                            this.options = mutableListOf(
                                Feature(
                                    featureId = 40000,
                                    name = "",
                                    description = ""
                                )
                            )
                        }
                    ),
                    spells = listOf(
                        Spell(
                            name = "Test",
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
                            id = 400
                        },
                        Spell(
                            name = "Test",
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
                            id = 401
                        }
                    )
                ),
                FeatureData(
                    Feature(
                        name = "UserOne Homebrew 2",
                        description = "",
                        featureId = 101
                    ),
                    spells = listOf(
                        Spell(
                            name = "Test",
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
                            id = 402
                        },
                        Spell(
                            name = "Test",
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
                            id = 403
                        }
                    )
                )
            ),
            indices = listOf(
                IndexRef(
                    index = "Test index",
                    ids = listOf(101)
                )
            )
        ),
        User(
            client = client2,
            features = listOf(
                FeatureData(
                    Feature(
                        name = "UserTwo Homebrew 1",
                        description = "",
                        featureId = 100
                    ),
                    spells = listOf(
                        Spell(
                            name = "Test",
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
                            id = 400
                        },
                        Spell(
                            name = "Test",
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
                            id = 405
                        }
                    )
                ),
                FeatureData(
                    Feature(
                        name = "UserTwo Homebrew 2",
                        description = "",
                        featureId = 101
                    )
                )
            )
        ),
    )

    @Test
    fun fillOutFeatureListWithoutChosen() = runTest {
        //TODO expand test.
        users.forEach { user ->
            user.features.forEach { entity ->
                user.featureService.insertFeature(entity.entity)
            }

            user.featureService.fillOutFeatureListWithoutChosen(user.features.map { it.entity})
        }
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
    fun insertAndRemoveFeatureOptionsCrossRef() = runTest {
        users.forEach { user ->
            user.features.forEach {feature ->
                user.featureService.insertFeature(feature.entity)

                feature.options.forEach { option ->
                    option.options?.forEach {
                        user.featureService.insertFeature(it)
                    }

                    user.featureService.insertFeatureChoice(option)

                    user.featureService.insertFeatureOptionsCrossRef(
                        featureId = feature.entity.featureId,
                        id = option.id
                    )

                    val features = user.featureService.getFeatureChoiceOptions(option.id)

                    assert(features.map { it.featureId } == option.options?.map { it.featureId} )
                }

                val options = user.featureService.getFeatureChoices(feature.entity.featureId)

                assert(options.map { it.id } == feature.options.map { it.id })
            }
        }
    }


    @Test
    fun insertAndClearFeatureChoiceIndexCrossRef() = runTest {
        //TODO Expand the functionally of this test.
        users.forEach { user ->
            user.featureService.insertFeatureChoiceIndexCrossRef(
                choiceId = 1000,
                index = "Test index",
                levels = listOf(0),
                classes = null,
                schools = null
            )

            user.featureService.clearFeatureChoiceIndexRefs(1000)
        }
    }

    @Test
    fun testIndexRef() = runTest {
        users.forEach { user ->
            user.indices.forEach { index ->
                val indexes = user.featureService.returnGetAllIndexes()

                user.featureService.insertIndexRef(index.index, index.ids)

                assert(indexes.first().contains(index.index))

                user.featureService.removeIdFromRef(index.ids[0], index.index)
            }
        }
    }

    @Test
    fun testFeatureSpells() = runTest {
        users.forEach { user ->
            user.features.forEach { entity ->
                user.featureService.insertFeature(entity.entity)

                entity.spells.forEach { spell ->
                    user.spellService.insertSpell(spell)

                    user.featureService.insertFeatureSpellCrossRef(
                        spellId = spell.id,
                        featureId = entity.entity.featureId
                    )
                }

                val spells = user.featureService.getLiveFeatureSpells(entity.entity.featureId)

                if(entity.spells.isNotEmpty())
                    assert(spells.first()?.map { it.id } == entity.spells.map { it.id } )

                entity.spells.forEach { spell ->
                    user.featureService.removeFeatureSpellCrossRef(
                        spellId = spell.id,
                        featureId = entity.entity.featureId
                    )
                }

                assert(spells.first()?.isEmpty() != false)
            }
        }
    }

    @Test
    fun insertOptionsFeatureCrossRef() = runTest {
        users.forEach { user ->
            user.features.forEach { entity ->
                user.featureService.insertFeature(entity.entity)

                entity.options.forEach { option ->
                    user.featureService.insertFeatureChoice(option)

                    option.options?.forEach { feature ->
                        user.featureService.insertFeature(feature)

                        user.featureService.insertOptionsFeatureCrossRef(
                            featureId = feature.featureId,
                            choiceId = option.id
                        )
                    }

                    val options = user.featureService.getFeatureChoiceOptions(option.id)

                    assert(options.map { it.featureId } == option.options?.map { it.featureId} )
                }
            }
        }
    }

    @Test
    fun getFeatureIdOr0FromSpellId() = runTest {
        users.forEach { user ->
            user.features.forEach { entity ->
                user.featureService.insertFeature(entity.entity)

                entity.spells.forEach { spell ->
                    user.spellService.insertSpell(spell)

                    user.featureService.insertFeatureSpellCrossRef(
                        spellId = spell.id,
                        featureId = entity.entity.featureId
                    )

                    val feature = user.featureService.getFeatureIdOr0FromSpellId(spell.id)
                    assert(feature == entity.entity.featureId)
                }
            }
        }
    }
}