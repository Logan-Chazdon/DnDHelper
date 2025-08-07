package gmail.loganchazdon.dndhelper.model.services

import app.cash.sqldelight.Query
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.gsonInstance
import gmail.loganchazdon.dndhelper.model.database.withUserInfo
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


private const val PATH = "sync"

fun Routing.pullSyncService(db: Database) {

    /**
     * Creates a get route for the given path that responds with a serialized execution of the provided query.
     * Passes authenticated userId as input to provided query.
     */
    fun <T : (String) -> Query<E>, E> Routing.getTable(path: String, query: T) {
        db.classesQueries.getAllClasses()
        get("$PATH/$path") {
            withUserInfo { user ->
                call.respondText(
                    gsonInstance.toJson(
                        query(user.id).executeAsList()
                    )
                )
            }
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    with(db) {
        getTable("characterTable") { characterQueries.selectAllFor(it) }
        getTable("backgroundTable") { backgroundsQueries.selectHomebrewBackgounds(it) }
        getTable("classTable") { classesQueries.getHomebrewClasses(it) }
        getTable("featureTable") { featuresQueries.selectByOwner(it) }
        getTable("raceTable") { racesQueries.selectHomebrewRaces(it) }
        getTable("spellTable") { spellsQueries.selectHomebrewFor(it) }
        getTable("subclassTable") { subclassesQueries.selectHomebrewSubclasses(it) }
        getTable("subraceTable") { subracesQueries.selectHomebrewByOwner(it) }


        getTable("characterBackgroundTable") { characterBackgroundCrossRefQueries.selectRefsFor(it) }
        getTable("backgroundChoiceTable") { backgroundChoiceEntityQueries.selectRefsFor(it) }
        getTable("backgroundFeatureTable") { backgroundFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("backgroundSpellTable") { backgroundSpellCrossRefQueries.selectRefsFor(it) }
        getTable("characterClassTable") { characterClassCrossRefQueries.selectRefsFor(it) }
        getTable("characterClassSpellTable") { characterClassSpellCrossRefQueries.selectRefsFor(it) }
        getTable("characterFeatureStateTable") { characterFeatureStateQueries.selectRefsFor(it) }
        getTable("characterRaceTable") { characterRaceCrossRefQueries.selectRefsFor(it) }
        getTable("characterSubclassTable") { characterSubclassCrossRefQueries.selectRefsFor(it) }
        getTable("characterSubraceTable") { characterSubraceCrossRefQueries.selectRefsFor(it) }
        getTable("classChoiceTable") { classChoiceEntityQueries.selectRefsFor(it) }
        getTable("classFeatTable") { classFeatCrossRefQueries.selectRefsFor(it) }
        getTable("classFeatureTable") { classFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("classSpellTable") { classSpellCrossRefQueries.selectRefsFor(it) }
        getTable("classSubclassTable") { classSubclassCrossRefQueries.selectRefsFor(it) }
        getTable("featChoiceChoiceTable") { featChoiceChoiceEntityQueries.selectRefsFor(it) }
        getTable("featChoiceFeatTable") { featChoiceFeatCrossRefQueries.selectRefsFor(it) }
        getTable("featFeatureTable") { featFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("featureChoiceChoiceTable") { featureChoiceChoiceEntityQueries.selectRefsFor(it) }
        getTable("featureOptionsTable") { featureOptionsCrossRefQueries.selectRefsFor(it) }
        getTable("featureSpellTable") { featureSpellCrossRefQueries.selectRefsFor(it) }
        getTable("optionsFeatureTable") { optionsFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("pactMagicStateTable") { pactMagicStateEntityQueries.selectRefsFor(it) }
        getTable("raceChoiceTable") { raceChoiceEntityQueries.selectRefsFor(it) }
        getTable("raceFeatureTable") { raceFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("raceSubraceTable") { raceSubraceCrossRefQueries.selectRefsFor(it) }
        getTable("subclassSpellCastingTable") { subclassSpellCastingSpellCrossRefQueries.selectRefsFor(it) }
        getTable("subclassSpellTable") { subclassSpellCrossRefQueries.selectRefsFor(it) }
        getTable("subraceChoiceTable") { subraceChoiceEntityQueries.selectRefsFor(it) }
        getTable("subraceFeatChoiceTable") { subraceFeatChoiceCrossRefQueries.selectRefsFor(it) }
        getTable("subraceFeatureTable") { subraceFeatureCrossRefQueries.selectRefsFor(it) }
        getTable("subclassFeatureTable") { subclassFeatureCrossRefQueries.selectRefsFor(it) }
    }
}