import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import model.*
import model.database.RoomDataBase
import model.database.converters.ItemConverter
import model.database.daos.*
import model.database.migrations.MIGRATION_56_57
import model.database.migrations.MIGRATION_57_58
import model.localDataSources.DataSource
import model.localDataSources.LocalDataSourceImpl
import model.sync.*
import model.sync.workers.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.annotation.Module
import org.koin.dsl.module
import services.PreferenceCookiesStorage
import services.PullSyncService
import ui.accounts.SignInManager


@Module
class AppModule {
    @OptIn(ExperimentalResourceApi::class)
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


        single {
            PullSyncService()
        }

        // Character Sync
        single<CharacterSyncManager> {
            CharacterSyncManager(androidApplication())
        }

        worker { PostCharacterWorker() }
        worker { DeleteCharacterWorker() }
        worker { PostCharacterNameWorker() }
        worker { PostCharacterIdealsWorker() }
        worker { PostCharacterTraitsWorker() }
        worker { PostCharacterNotesWorker() }
        worker { PostCharacterFlawsWorker() }
        worker { PostCharacterBondsWorker() }
        worker { PostCharacterPactMagicStateEntityWorker() }
        worker { PostCharacterSubraceCrossRefWorker() }
        worker { PostSubraceChoiceEntityWorker() }
        worker { PostCharacterSubclassCrossRefWorker() }
        worker { PostFeatureChoiceEntityWorker() }
        worker { PostCharacterClassSpellCrossRefWorker() }
        worker { PostSubclassSpellCastingSpellCrossRefWorker() }
        worker { PostCharacterBackpackWorker() }
        worker { DeleteClassCharacterCrossRefWorker() }
        worker { PostCharacterClassCrossRefWorker() }
        worker { PostClassChoiceEntityWorker() }
        worker { PostCharacterClassFeatCrossRefWorker() }
        worker { PostBackgroundChoiceEntityWorker() }
        worker { PostRaceChoiceWorker() }
        worker { PostCharacterRaceCrossRefWorker() }
        worker { PostCharacterBackgroundCrossRefWorker() }
        worker { PostCharacterTempWorker() }
        worker { PostCharacterHealWorker() }
        worker { PostCharacterHpWorker() }
        worker { PostCharacterDamageWorker() }
        worker { PostDeathSaveSuccessWorker() }
        worker { PostDeathSaveFailureWorker() }
        worker { PostSpellSlotsWorker() }
        worker { DeleteCharacterClassSpellCrossRefsWorker() }
        worker { PostCharacterFeatureStateWorker() }
        worker { DeleteFeatureFeatureChoiceWorker() }


        // Class Sync
        single { ClassSyncManager(androidApplication()) }

        worker { PostClassWorker() }
        worker { DeleteClassWorker() }
        worker { PostClassFeatureCrossRefWorker() }
        worker { PostSubclassWorker() }
        worker { DeleteSubclassFeatureCrossRefWorker() }
        worker { PostSubclassFeatureCrossRefWorker() }
        worker { DeleteClassFeatureCrossRefWorker() }
        worker { DeleteClassSubclassCrossRefWorker() }
        worker { DeleteSubclassWorker() }

        // Feature Sync
        single { FeatureSyncManager(androidApplication()) }

        worker { PostFeatureWorker() }
        worker { PostOptionsFeatureCrossRefWorker() }
        worker { PostFeatureChoiceWorker() }
        worker { DeleteFeatureOptionsCrossRefWorker() }
        worker { DeleteOptionsFeatureCrossRefWorker() }
        worker { ClearFeatureChoiceIndexRefsWorker() }
        worker { PostFeatureChoiceIndexCrossRefWorker() }
        worker { PostIndexRefWorker() }
        worker { DeleteIdFromRefWorker() }
        worker { PostFeatureSpellCrossRefWorker() }
        worker { DeleteFeatureSpellCrossRefWorker() }

        // Race Sync
        single { RaceSyncManager(androidApplication()) }

        worker { PostRaceWorker() }
        worker { DeleteRaceWorker() }
        worker { PostRaceFeatureCrossRefWorker() }
        worker { DeleteRaceFeatureCrossRefWorker() }
        worker { PostSubraceFeatureCrossRefWorker() }
        worker { DeleteSubraceFeatureCrossRefWorker() }
        worker { PostRaceSubraceCrossRefWorker() }
        worker { PostSubraceWorker() }
        worker { DeleteSubraceWorker() }
        worker { DeleteRaceSubraceCrossRefWorker() }

        // Spell Sync
        single { SpellSyncManager(androidApplication()) }

        worker { PostSpellWorker() }
        worker { DeleteClassSpellCrossRefWorker() }
        worker { PostClassSpellCrossRefWorker() }
        worker { DeleteSpellWorker() }


        // Background Sync
        single { BackgroundSyncManager(androidApplication()) }

        worker { PostBackgroundWorker() }
        worker { PostBackgroundFeatureCrossRefWorker() }
        worker { DeleteBackgroundWorker() }

        single { PullSyncManager(androidApplication()) }

        single<HttpClient> {
            HttpClient {
                install(HttpCookies) {
                    storage = PreferenceCookiesStorage(androidApplication())
                }
            }
        }

        single<SignInManager> {
            SignInManager()
        }

        single<Gson> {
            val converter = ItemConverter("type")
            converter.registerItemType("Item", Item::class.java)
            converter.registerItemType("Weapon", Weapon::class.java)
            converter.registerItemType("Armor", Armor::class.java)
            converter.registerItemType("Currency", Currency::class.java)
            converter.registerItemType("Shield", Shield::class.java)
            GsonBuilder()
                .registerTypeAdapter(ItemInterface::class.java, converter)
                .create()
        }
    }
}
