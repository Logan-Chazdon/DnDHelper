package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.ClassEntity
import model.FeatureEntity
import model.SubclassEntity
import org.junit.Test


class SubclassServiceIntegrationTest {
    private data class User(
        val subclasses: List<SubclassData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class SubclassData(
        val entity: SubclassEntity,
        val features: List<FeatureEntity> = emptyList(),
        val classes: List<ClassEntity> = emptyList()
    )

    private val users = listOf(
        User(
            client = client1,
            subclasses = listOf(
                SubclassData(
                    SubclassEntity(
                        name = "UserOne Homebrew",
                        spellAreFree = false,
                        spellCasting = null,
                        isHomebrew = true,
                    ).apply {
                        subclassId = 100
                    },
                    features = listOf(FeatureEntity(featureId = 2200, name = "Test", description = "")),
                    classes = listOf(ClassEntity(id= 2000))
                ),
                SubclassData(
                    SubclassEntity(
                        name = "UserOne Homebrew",
                        spellAreFree = false,
                        spellCasting = null,
                        isHomebrew = true,
                    ).apply {
                        subclassId = 101
                    },
                    classes = listOf(ClassEntity(id= 2001)))
            ),
        ),
        User(
            client = client2,
            subclasses = listOf(
                SubclassData(
                    SubclassEntity(
                        name = "UserTwo Homebrew",
                        spellAreFree = false,
                        spellCasting = null,
                        isHomebrew = true,
                    ).apply {
                        subclassId = 100
                    },
                    features = listOf(FeatureEntity(featureId = 2200, name = "Test", description = "")),
                    classes = listOf(ClassEntity(id= 2000))
                ),
                SubclassData(
                    SubclassEntity(
                        name = "UserTwo Homebrew",
                        spellAreFree = false,
                        spellCasting = null,
                        isHomebrew = true,
                    ).apply {
                        subclassId = 101
                    },
                    classes = listOf(ClassEntity(id= 2001))
                )
            ),
        ),
    )

    @Test
    fun getSubclassesByClassId() = runTest {
        users.forEach { user ->
            user.subclasses.forEach { subclass ->
                user.subclassService.insertSubclass(subclass.entity)
                subclass.classes.forEach { clazz ->
                    user.classService.insertClass(clazz)

                    user.classService.insertClassSubclassId(
                        classId = clazz.id,
                        subclassId = subclass.entity.subclassId
                    )

                    val serverClasses = user.subclassService.getSubclassesByClassId(clazz.id).first()

                    assert(serverClasses.firstOrNull { it.subclassId == subclass.entity.subclassId } != null)
                }
            }
        }
    }

    @Test
    fun insertAndGetSubclass() = runTest {
        users.forEach { user ->
            user.subclasses.forEach { subclass ->
                user.subclassService.insertSubclass(subclass.entity)
                val serverSubclass = user.subclassService.getSubclass(subclass.entity.subclassId).first()
                assert(subclass.entity.name == serverSubclass.name)
            }
        }
    }


    @Test
    fun subclassFeatureCrossRefTest() = runTest {
        users.forEach { user ->
            user.subclasses.forEach { subclass ->
                //Ensure the subclass is in the db.
                user.subclassService.insertSubclass(subclass.entity)

                //Insert all the features and connect them to the subclass.
                subclass.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.subclassService.insertSubclassFeatureCrossRef(
                        subclassId = subclass.entity.subclassId,
                        featureId = feature.featureId
                    )
                }

                //Check that the features are returned.
                val serverFeatures = user.subclassService.getSubclassFeatures(
                    subclass.entity.subclassId,
                    maxLevel = 20
                )

                assert(serverFeatures.map { it.featureId } == subclass.features.map { it.featureId })

                val liveServerFeatures = user.subclassService.getSubclassLiveFeaturesById(subclass.entity.subclassId)

                assert(liveServerFeatures.first().map { it.featureId } == subclass.features.map { it.featureId })


                subclass.features.forEach { feature ->
                    //Delete each connection.
                    user.subclassService.removeSubclassFeatureCrossRef(
                        subclassId = subclass.entity.subclassId,
                        featureId = feature.featureId
                    )

                    //Ensure the features are disconnected.
                    assert(
                        liveServerFeatures.first().firstOrNull
                        { it.featureId == feature.featureId } == null
                    )

                    val refreshedServerFeatures = user.subclassService.getSubclassFeatures(
                        subclass.entity.subclassId,
                        maxLevel = 20
                    )


                    assert(
                        refreshedServerFeatures.firstOrNull
                        { it.featureId == feature.featureId } == null
                    )
                }
            }
        }
    }

    @Test
    fun getHomebrewSubclasses() = runTest {
        users.forEach { user ->
            user.subclasses.forEach { subclass ->
                user.subclassService.insertSubclass(subclass.entity)
            }

            val subclasses = user.subclassService.getHomebrewSubclasses().first()
            assert(subclasses.size == 2)
        }
    }

    @Test
    fun deleteSubclass() = runTest {
        users.forEach { user ->
            user.subclasses.forEach { subclass ->
                user.subclassService.insertSubclass(subclass.entity)
            }

            user.subclasses.forEach { subclass ->
                user.subclassService.deleteSubclass(subclass.entity.subclassId)
            }

            val subclasses = user.subclassService.getHomebrewSubclasses().first()
            assert(subclasses.isEmpty())
        }
    }
}