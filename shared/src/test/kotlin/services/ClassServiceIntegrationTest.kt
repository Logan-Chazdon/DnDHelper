package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.ClassEntity
import model.FeatureEntity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ClassServiceIntegrationTest {

    private data class User(
        val classes: List<ClassData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class ClassData(
        val entity: ClassEntity,
        val features: List<FeatureEntity>
    )

    private val users = listOf(
        User(
            client = client1,
            classes = listOf(
                ClassData(
                    ClassEntity(
                        name = "userOneClass",
                        id = 14
                    ),
                    listOf(
                        FeatureEntity(
                            name = "Test1",
                            description = "",
                            featureId = 5000
                        ),
                        FeatureEntity(
                            name = "user1",
                            description = "",
                            featureId = 5001
                        )
                    )
                )
            )
        ),
        User(
            client = client1,
            classes = listOf(
                ClassData(
                    ClassEntity(
                        name = "userTwoClass",
                        id = 14
                    ),
                    listOf(
                        FeatureEntity(
                            name = "user2",
                            description = "",
                            featureId = 5001
                        )
                    )
                )
            )
        )
    )


    @Test
    fun getAllClasses() = runTest {
        users.forEach { user ->
            val classes = user.classService.getAllClasses().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun getUnfilledClass() = runTest {
        users.forEach { user ->
            (1..13).forEach { id ->
                val clazz = user.classService.getUnfilledClass(id).first()
                assert(clazz.id == id)
            }
        }
    }

    @Test
    fun insertClass() = runTest {
        users.forEach { user ->
            user.classes.forEach { clazz ->
                val id = user.classService.insertClass(clazz.entity)
                val real = user.classService.getUnfilledClass(id).first()

                assertEquals(real.name, clazz.entity.name)
            }
        }
    }

    @Test
    fun testClassFeatureCrossRef() = runTest {
        users.forEach { user ->
            user.classes.forEach { clazz ->
                user.classService.insertClass(clazz.entity)

                clazz.features.forEach { feature ->
                    user.featureService.insertFeature(feature)

                    user.classService.insertClassFeatureCrossRef(
                        featureId = feature.featureId,
                        id = clazz.entity.id
                    )
                }

                var features = user.classService.getUnfilledLevelPath(clazz.entity.id)


                assert(
                    features.map { it.featureId } == (clazz.features.map { it.featureId })
                )

                clazz.features.forEach { feature ->
                    user.classService.removeClassFeatureCrossRef(
                        featureId = feature.featureId,
                        id = clazz.entity.id
                    )
                }

                features = user.classService.getUnfilledLevelPath(clazz.entity.id)

                assert(features.isEmpty())
            }
        }
    }

    @Test
    fun insertClassSubclassId() {
    }



    @Test
    fun removeClassSubclassCrossRef() {
    }

    @Test
    fun getClassIdsByName() = runTest {
        users.forEach { user ->
            user.classes.forEach { clazz ->
                user.classService.insertClass(clazz.entity)
                val ids = user.classService.getClassIdsByName(clazz.entity.name)
                assert(ids.contains(clazz.entity.id))
            }
        }
    }

    @Test
    fun getHomebrewClasses() = runTest {
        users.forEach { user ->
            val classes = user.classService.getAllClasses().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun deleteClass() = runTest {
        users.forEach { user ->
            val id = user.classService.insertClass(ClassEntity("Fake class"))
            user.classService.deleteClass(id)
            val classes = user.classService.getAllClasses().first()
            assert(classes.none { it.name == "Fake class" })
        }
    }

    @Test
    fun getSpellsByClassId() {
    }

    @Test
    fun allClassesNamesAndIds() = runTest {
        users.forEach { user ->
            val classes = user.classService.allClassesNamesAndIds().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun getSubclassClasses() {
    }
}