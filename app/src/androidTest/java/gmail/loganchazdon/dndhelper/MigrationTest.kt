package gmail.loganchazdon.dndhelper

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import gmail.loganchazdon.dndhelper.model.database.Converters
import gmail.loganchazdon.dndhelper.model.database.MIGRATION_56_57
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"
    private val converters = Converters()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RoomDataBase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate56_57() {
        //Create a database at version 56 and insert a version 56 character.
        helper.createDatabase(TEST_DB, 56).apply {
            this.execSQL(insertVersion56CharacterSQL)
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 57, true, MIGRATION_56_57)
        val cursor = db.query("SELECT * FROM characters", arrayOf())
        cursor.moveToFirst()

        val raceJson = cursor.getString(6)
        val backgroundJson = cursor.getString(15)
        val classesJson = cursor.getString(11)

        val race = converters.storedStringToRace(raceJson)
        val background = converters.storedStringToBackground(backgroundJson)
        val classes = converters.storedStringToClasses(classesJson)

        //Check that the artificers infusions have been preserved.
        classes.forEach {
            assert(it.value.levelPath[1].choices!![0].chosen!!.size == 4)
        }

        //Check that the dragonborn's type has been preserved.
        assert(race!!.traits[0].choices!![0].chosen!!.size == 1)
    }
}
