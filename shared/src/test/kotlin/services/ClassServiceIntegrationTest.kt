package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.ClassEntity
import model.FeatureEntity
import model.Spell
import model.SubclassEntity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ClassServiceIntegrationTest {

    private data class User(
        val classes: List<ClassData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class ClassData(
        val entity: ClassEntity,
        val features: List<FeatureEntity>,
        val subclasses: List<SubclassEntity>,
        val spells: List<Spell>
    )

    private val defaultSpell = Spell(
        name = "Default Spell",
        level = 1,
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
        id = 10000
    }

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
                    ),
                    listOf(
                        SubclassEntity(
                            name = "test",
                            spellAreFree = false,
                            spellCasting = null,
                            isHomebrew = true,
                        ).apply {
                            subclassId = 1000
                        }
                    ),
                    listOf(defaultSpell)
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
                    ),
                    listOf(
                        SubclassEntity(
                            name = "test",
                            spellAreFree = false,
                            spellCasting = null,
                            isHomebrew = true,
                        ).apply {
                            subclassId = 1000
                        }
                    ),
                    listOf(defaultSpell)
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
    fun testClassSubclassCrossRef() = runTest {
        users.forEach { user ->
            user.classes.forEach { clazz ->
                user.classService.insertClass(clazz.entity)

                //Test adding the cross refs.
                clazz.subclasses.forEach {
                    user.subclassService.insertSubclass(it)

                    user.classService.insertClassSubclassId(
                        classId = clazz.entity.id,
                        subclassId = it.subclassId
                    )
                }

                val subclasses = user.subclassService.getSubclassesByClassId(clazz.entity.id)

                //Test that the subclasses are returned.
                clazz.subclasses.forEach { subclass ->
                    assert(subclasses.first().firstOrNull {
                        subclass.subclassId == it.subclassId &&
                                subclass.name == it.name
                    } != null)
                }

                clazz.subclasses.forEach { subclass ->
                    val classes = user.classService.getSubclassClasses(subclass.subclassId).first()
                    assert(classes.firstOrNull { it.id == clazz.entity.id } != null)
                }

                //Test deleting the cross refs.
                clazz.subclasses.forEach { subclass ->
                    user.classService.removeClassSubclassCrossRef(
                        classId = clazz.entity.id,
                        subclassId = subclass.subclassId
                    )


                    assert(subclasses.first().firstOrNull {
                        subclass.subclassId == it.subclassId
                    } == null)
                }
            }
        }
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
    fun getSpellsByClassId() = runTest {
        users.forEach { user ->
            user.classes.forEach { clazz ->
                user.classService.insertClass(clazz.entity)
                clazz.spells.forEach { spell ->
                    user.spellService.insertSpell(spell)
                    user.spellService.addClassSpellCrossRef(
                        classId = clazz.entity.id,
                        spellId = spell.id
                    )
                }

                val spells = user.classService.getSpellsByClassId(clazz.entity.id)
                assert(spells.map { it.id } == clazz.spells.map { it.id } )
            }
        }
    }

    @Test
    fun allClassesNamesAndIds() = runTest {
        users.forEach { user ->
            val classes = user.classService.allClassesNamesAndIds().first()

            assert(classes.size >= 13)
        }
    }
}