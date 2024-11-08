package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.ClassEntity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ClassServiceIntegrationTest {
    private val userOneService = ClassService(client1)
    private val userTwoService = ClassService(client2)

    private val users = listOf(
        userOneService to listOf(
            ClassEntity(
                name = "userOneClass",
                id = 14
            )
        ),
        userTwoService to listOf(
            ClassEntity(
                name = "userTwoClass",
                id = 14
            )
        )
    )


    @Test
    fun getAllClasses() = runTest {
        users.forEach { user ->
            val classes = user.first.getAllClasses().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun getUnfilledClass() = runTest {
        users.forEach { user ->
            (1..13).forEach { id ->
                val clazz = user.first.getUnfilledClass(id).first()
                assert(clazz.id == id)
            }
        }
    }

    @Test
    fun insertClass() = runTest {
        users.forEach { user ->
            user.second.forEach { clazz ->
                val id = user.first.insertClass(clazz)
                val real = user.first.getUnfilledClass(id).first()

                assertEquals(real.name, clazz.name)
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
            val classes = user.first.getAllClasses().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun deleteClass() = runTest {
        users.forEach { user ->
            val id = user.first.insertClass(ClassEntity("Fake class"))
            user.first.deleteClass(id)
            val classes = user.first.getAllClasses().first()
            assert(classes.none { it.name == "Fake class"})
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
            val classes = user.first.allClassesNamesAndIds().first()

            assert(classes.size >= 13)
        }
    }

    @Test
    fun getSubclassClasses() {
    }
}