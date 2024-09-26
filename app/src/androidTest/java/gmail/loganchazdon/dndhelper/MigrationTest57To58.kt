package gmail.loganchazdon.dndhelper

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import model.database.RoomDataBase
import model.database.migrations.MIGRATION_57_58
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MigrationTest57To58 {
    private val TEST_DB = "migration-test-57-58"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RoomDataBase::class.java
    )
    private lateinit var db: SupportSQLiteDatabase

    @Before
    fun setup() {
        helper.createDatabase(TEST_DB, 57).apply {
            this.execSQL(insertVersion57Character1SQL)
            this.execSQL(insertVersion57Character2SQL)
            this.execSQL(insertVersion57Character3SQL)
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 58, true, MIGRATION_57_58)
    }

    @Test
    fun charactersIsCorrect() {
        val cursor = db.query("SELECT * FROM characters")
        var i = 0
        val nameIndex = cursor.getColumnIndex("name")
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val name = cursor.getString(nameIndex)
            i++
            assert(name == "Character $i")
            cursor.moveToNext()
        }
    }
}