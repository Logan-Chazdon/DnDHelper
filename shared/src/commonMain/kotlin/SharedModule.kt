import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import model.repositories.*
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import services.CharacterService
import ui.character.*
import ui.homebrew.*
import ui.newCharacter.*

@Module
class SharedModule {
    val module = module {
        viewModel { AllCharactersViewModel(get()) }
        viewModel { CharacterMainViewModel(get(), get()) }
        viewModel { CombatViewModel(get(), get()) }
        viewModel { ItemDetailsViewModel(get(), get()) }
        viewModel { ItemViewModel(get(), get(), get()) }
        viewModel { StatsViewModel(get(), get(), get()) }
        viewModel { HomebrewBackgroundViewModel(get(), get(), get(), get()) }
        viewModel { HomebrewClassViewModel(get(), get(), get(), get()) }
        viewModel { HomebrewRaceViewModel(get(), get(), get()) }
        viewModel { HomebrewSpellViewModel(get(), get(), get(), get()) }
        viewModel { HomebrewViewModel(get(), get(), get(), get(), get()) }
        viewModel { SubclassViewModel(get(), get(), get()) }
        viewModel { SubraceViewModel(get(), get(), get()) }
        viewModel { NewCharacterBackgroundViewModel(get(), get(), get()) }
        viewModel { NewCharacterConfirmBackgroundViewModel(get(), get(), get()) }
        viewModel { NewCharacterClassViewModel(get(), get(), get()) }
        viewModel { NewCharacterConfirmClassViewModel(get(), get(), get(), get()) }
        viewModel { NewCharacterConfirmRaceViewModel(get(), get(), get()) }
        viewModel { NewCharacterStatsViewModel(get(), get()) }
        viewModel { NewCharacterRaceViewModel(get(), get()) }



        single<CharacterRepository> {
            CharacterRepository(
                characterDao = get(),
                raceDao = get(),
                backgroundDao = get(),
                classDao = get(),
                subclassDao = get(),
                featureDao = get()
            )
        }


        single<BackgroundRepository> { BackgroundRepository(get(), get()) }
        single<CharacterRepository> { CharacterRepository(get(), get(), get(), get(), get(), get()) }
        single<ClassRepository> { ClassRepository(get(), get(), get()) }
        single<FeatRepository> { FeatRepository(get(), get()) }
        single<FeatureRepository> { FeatureRepository(get(), get()) }
        single<ItemRepository> { ItemRepository(get()) }
        single<ProficiencyRepository> { ProficiencyRepository(get()) }
        single<RaceRepository> { RaceRepository(get(), get(), get()) }
        single<SpellRepository> { SpellRepository(get()) }

        single {
            HttpClient {
                install(WebSockets) {
                    pingIntervalMillis = 20_000
                }
            }
        }

        single { CharacterService(get()) }
    }
}