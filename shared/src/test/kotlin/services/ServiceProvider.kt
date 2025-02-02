package services

import io.ktor.client.*

abstract class ServiceProvider(client: HttpClient) {
    val subclassService = SubclassService(client)
    val featureService = FeatureService(client)
    val classService = ClassService(client)
    val backgroundService = BackgroundService(client)
    val characterService = CharacterService(client)
    val featService = FeatService(client)
    val raceService = RaceService(client)
    val spellService = SpellService(client)
    val subraceService = SubraceService(client)
}