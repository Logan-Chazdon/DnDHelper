package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.ClassEntity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ClassServiceIntegrationTest {

    private data class User(
        val classes: List<ClassData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class ClassData(
        val entity: ClassEntity,
    )

    private val users = listOf(
        User(
            client = client1,
            classes =listOf(
                ClassData(
                    ClassEntity(
                        name = "userOneClass",
                        id = 14
                    )
                )
            )
        ),
        User(
            client = client1,
            classes =listOf(
                ClassData(
                    ClassEntity(
                        name = "userTwoClass",
                        id = 14
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
    fun insertClassFeatureCrossRef() {
    }

    @Test
    fun insertClassSubclassId() {
    }

    @Test
    fun removeClassFeatureCrossRef() {
    }

    @Test
    fun removeClassSubclassCrossRef() {
    }

    @Test
    fun getClassIdsByName() = runTest {

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
    fun getUnfilledLevelPath() {
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