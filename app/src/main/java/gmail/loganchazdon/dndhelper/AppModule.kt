package gmail.loganchazdon.dndhelper

import androidx.room.Room
import model.database.RoomDataBase
import model.database.daos.*
import model.database.migrations.MIGRATION_56_57
import model.database.migrations.MIGRATION_57_58
import model.localDataSources.DataSource
import model.localDataSources.LocalDataSourceImpl
import org.koin.core.annotation.Module
import org.koin.dsl.module


@Module
class AppModule {
    val module = module {
        single<DataSource>(createdAtStart = true) {
            LocalDataSourceImpl(
                context = get(),
                backgroundDao = get(),
                classDao = get(),
                featDao = get(),
                raceDao = get(),
                spellDao = get(),
                featureDao = get(),
                subraceDao = get(),
                subclassDao = get(),
                db = get()
            )
        }

        single<BackgroundDao> {
            val db: RoomDataBase = get()
            db.backgroundDao()
        }

        single<CharacterDao> {
            val db: RoomDataBase = get()
            db.characterDao()
        }

        single<ClassDao> {
            val db: RoomDataBase = get()
            db.classDao()
        }

        single<FeatDao> {
            val db: RoomDataBase = get()
            db.featDao()
        }
        single<FeatureDao> {
            val db: RoomDataBase = get()
            db.featureDao()
        }
        single<RaceDao> {
            val db: RoomDataBase = get()
            db.raceDao()
        }
        single<SpellDao> {
            val db: RoomDataBase = get()
            db.spellDao()
        }
        single<SubclassDao> {
            val db: RoomDataBase = get()
            db.subclassDao()
        }
        single<SubraceDao> {
            val db: RoomDataBase = get()
            db.subraceDao()
        }


        single<RoomDataBase> {
            Room.databaseBuilder(
                get(),
                RoomDataBase::class.java,
                "database"
            ).addMigrations(MIGRATION_56_57, MIGRATION_57_58).fallbackToDestructiveMigration().build()
        }
    }
}
