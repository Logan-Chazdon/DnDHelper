package gmail.loganchazdon.dndhelper

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import gmail.loganchazdon.dndhelper.model.database.migrations.MIGRATION_57_58
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MigrationTest57To58 {
    private val TEST_DB = "migration-test-57-58"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RoomDataBase::class.java
    )


    @Test
    @Throws(IOException::class)
    fun migrate57_58() {
        helper.createDatabase(TEST_DB, 57).apply {
            execSQL(insertVersion57CharacterSQL)
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 58, true, MIGRATION_57_58)

        //Validate that the character table is correct
        val characterCursor = db.query("SELECT * FROM characters")
        assert(characterCursor.moveToFirst())
        
        //Validate that class choices are correct
        val classChoiceCursor = db.query("SELECT * FROM ClassChoiceEntity")
        assert(classChoiceCursor.moveToFirst())

        //Validate that race choices are correct
        val raceChoiceCursor = db.query("SELECT * FROM RaceChoiceEntity")
        assert(raceChoiceCursor.moveToFirst())

        //Validate that background choices are correct
        val backgroundChoiceCursor = db.query("SELECT * FROM BackgroundChoiceEntity")
        assert(backgroundChoiceCursor.moveToFirst())
    }
}