package model.sync.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import model.asTable
import model.database.daos.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.*


class PullSyncWorker(
    appContext: Context,
    workParams: WorkerParameters,
) : CoroutineWorker(appContext, workParams), KoinComponent {
    private val pullSyncService: PullSyncService by inject()
    private val characterService : CharacterService by inject()
    private val backgroundService: BackgroundService by inject()
    private val classService: ClassService by inject()
    private val featureService: FeatureService by inject()
    private val raceService: RaceService by inject()
    private val spellService: SpellService by inject()
    private val subclassService: SubclassService by inject()
    private val subraceService: SubraceService by inject()


    private val characterDao: CharacterDao by inject()
    private val classDao: ClassDao by inject()
    private val raceDao: RaceDao by inject()
    private val featureDao: FeatureDao by inject()
    private val backgroundDao: BackgroundDao by inject()
    private val featDao: FeatDao by inject()
    private val subraceDao: SubraceDao by inject()
    private val subclassDao: SubclassDao by inject()
    private val spellDao: SpellDao by inject()


    private val shouldPost = inputData.getBoolean("pushLocal", false)

    override suspend fun doWork(): Result {
        syncTable(
            serverList = pullSyncService.characterTable(),
            localList = characterDao.characterTable(),
            comparator = { a, b -> a.id == b.id },
            insert = { characterDao.insertCharacter(it) },
            delete = { characterDao.deleteCharacter(it.id) },
            post = { characterService.postCharacter(it) }
        )

        syncTable(
            serverList = pullSyncService.backgroundTable(),
            localList = backgroundDao.backgroundTable(),
            comparator = { a, b -> a.id == b.id },
            insert = { backgroundDao.insertBackground(it) },
            delete = { backgroundDao.deleteBackground(it.id) },
            post = { backgroundService.insertBackground(it) },
        )

        syncTable(
            serverList = pullSyncService.classTable(),
            localList = classDao.classTable(),
            comparator = { a, b -> a.id == b.id },
            insert = { classDao.insertClass(it) },
            delete = { classDao.deleteClass(it.id) },
            post = { classService.insertClass(it) }
        )

        syncTable(
            serverList = pullSyncService.featureTable(),
            localList = featureDao.featureTable(),
            comparator = { a, b -> a.featureId == b.featureId },
            insert = { it.isHomebrew = true; featureDao.insertFeature(it) },
            delete = { featureDao.deleteFeature(it.featureId) },
            post = { featureService.insertFeature(it) }
        )

        syncTable(
            serverList = pullSyncService.raceTable(),
            localList = raceDao.raceTable(),
            comparator = { a, b -> a.raceId == b.raceId },
            insert = { raceDao.insertRace(it) },
            delete = { raceDao.deleteRace(it.raceId) },
            post = { raceService.insertRace(it) }
        )

        syncTable(
            serverList = pullSyncService.spellTable(),
            localList = spellDao.spellTable(),
            comparator = { a, b -> a.id == b.id },
            insert = { spellDao.insertSpell(it) },
            delete = { spellDao.removeSpellById(it.id) },
            post = { spellService.insertSpell(it) }
        )

        syncTable(
            serverList = pullSyncService.subclassTable(),
            localList = subclassDao.subclassTable(),
            comparator = { a, b -> a.subclassId == b.subclassId },
            insert = { subclassDao.insertSubclass(it) },
            delete = { subclassDao.deleteSubclass(it.subclassId) },
            post = { subclassService.insertSubclass(it) }
        )

        syncTable(
            serverList = pullSyncService.subraceTable(),
            localList = subraceDao.subraceTable(),
            comparator = { a, b -> a.id == b.id },
            insert = { subraceDao.insertSubrace(it) },
            delete = { subraceDao.deleteSubrace(it.id) },
            post = { subraceService.insertSubrace(it) }
        )

        // TODO: Check feats and choice entities


        syncTable(
            serverList = pullSyncService.characterBackgroundTable(),
            localList = backgroundDao.getCharacterBackgroundTable(),
            comparator = { a, b -> a.backgroundId == b.backgroundId && a.characterId == b.characterId },
            insert = { characterDao.insertCharacterBackgroundCrossRef(it) },
            delete = { characterDao.removeCharacterBackgroundCrossRef(it) },
            post = { characterService.insertCharacterBackgroundCrossRef(
                backgroundId = it.backgroundId,
                characterId = it.characterId
            ) }
        )


        // Junction tables and state entities

        syncTable(
            serverList = pullSyncService.backgroundChoiceTable(),
            localList = backgroundDao.backgroundChoiceTable(),
            comparator = { a, b -> a.backgroundId == b.backgroundId && a.characterId == b.characterId },
            insert = { characterDao.insertBackgroundChoiceEntity(it) },
            delete = {  },
            post = { characterService.insertBackgroundChoiceEntity(it) }
        )

        syncTable(
            serverList = pullSyncService.backgroundFeatureTable(),
            localList = backgroundDao.backgroundFeatureTable(),
            comparator = { a, b -> a.backgroundId == b.backgroundId && a.featureId == b.featureId },
            insert = { backgroundDao.insertBackgroundFeatureCrossRef(it) },
            delete = { backgroundDao.removeBackgroundFeatureCrossRef(it) },
            post = { backgroundService.insertBackgroundFeatureCrossRef(
                featureId = it.featureId, backgroundId =  it.backgroundId
            ) },
        )

        syncTable(
            serverList = pullSyncService.backgroundSpellTable(),
            localList = backgroundDao.backgroundSpellTable(),
            comparator = { a, b -> a.backgroundId == b.backgroundId && a.spellId == b.spellId },
            insert = { backgroundDao.insertBackgroundSpellCrossRef(it) },
            delete = { backgroundDao.removeBackgroundSpellCrossRef(it) },
            post = { backgroundService.insertBackgroundSpellCrossRef(
                backgroundId = it.backgroundId,
                spellId = it.spellId
            )}
        )

        syncTable(
            serverList = pullSyncService.characterClassTable(),
            localList = characterDao.characterClassTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.classId == b.classId },
            insert = { characterDao.insertCharacterClassCrossRef(it) },
            delete = { characterDao.removeCharacterClassCrossRef(it) },
            post = { characterService.insertCharacterClassCrossRef(
                characterId = it.characterId,
                classId = it.classId
            )}
        )

        syncTable(
            serverList = pullSyncService.characterClassSpellTable(),
            localList = characterDao.characterClassSpellTable(),
            comparator = { a, b ->
                a.characterId == b.characterId &&
                        a.classId == b.classId &&
                        a.spellId == b.spellId
            },
            insert = { characterDao.insertCharacterClassSpellCrossRef(it) },
            delete = { characterDao.removeCharacterClassSpellCrossRef(it) },
            post = { characterService.insertCharacterClassSpellCrossRef(
                classId = it.classId,
                spellId = it.spellId,
                characterId = it.characterId,
                prepared = it.isPrepared
            ) }
        )

        syncTable(
            serverList = pullSyncService.characterFeatureStateTable(),
            localList = characterDao.characterFeatureStateTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.featureId == b.featureId },
            insert = { characterDao.insertCharacterFeatureState(it) },
            delete = { },
            post = { characterService.insertCharacterFeatureState(
                characterId = it.characterId,
                active = it.isActive,
                featureId = it.featureId,
            ) }
        )

        syncTable(
            serverList = pullSyncService.characterRaceTable(),
            localList = characterDao.characterRaceTable(),
            comparator = { a, b -> a.id == b.id && a.raceId == b.raceId },
            insert = { characterDao.insertCharacterRaceCrossRef(it) },
            delete = { characterDao.removeCharacterRaceCrossRef(it) },
            post = { characterService.insertCharacterRaceCrossRef(
                id = it.id,
                raceId = it.raceId
            ) }
        )

        syncTable(
            serverList = pullSyncService.characterSubclassTable(),
            localList = characterDao.characterSubclassTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.classId == b.classId && a.subClassId == b.subClassId },
            insert = { characterDao.insertCharacterSubclassCrossRef(it) },
            delete = { },
            post = { characterService.insertCharacterSubclassCrossRef(
                subClassId = it.subClassId,
                characterId = it.characterId,
                classId = it.classId
            )}
        )

        syncTable(
            serverList = pullSyncService.characterSubraceTable(),
            localList = characterDao.characterSubraceTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.subraceId == b.subraceId },
            insert = { characterDao.insertCharacterSubRaceCrossRef(it) },
            delete = { },
            post = { characterService.insertCharacterSubraceCrossRef(
                characterId = it.characterId,
                subraceId = it.subraceId
            )}
        )

        syncTable(
            serverList = pullSyncService.classChoiceTable(),
            localList = characterDao.classChoiceTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.classId == b.classId },
            insert = { characterDao.insertClassChoiceEntity(it) },
            delete = {
                characterDao.removeClassChoiceEntity(
                    classId = it.classId,
                    characterId = it.characterId
                )
            },
            post = { characterService.insertClassChoiceEntity(it) }
        )

        syncTable(
            serverList = pullSyncService.classFeatTable(),
            localList = characterDao.getClassFeatTable(),
            comparator = { a, b ->
                a.characterId == b.characterId &&
                        a.classId == b.classId &&
                        a.featId == b.featId
            },
            insert = { characterDao.insertCharacterClassFeatCrossRef(it) },
            delete = { characterDao.removeCharacterClassFeatCrossRef(it) },
            post = { characterService.insertCharacterClassFeatCrossRef(
                characterId = it.characterId,
                featId = it.featId,
                classId = it.classId
            )}
        )

        syncTable(
            serverList = pullSyncService.classFeatureTable(),
            localList = classDao.classFeatureTable(),
            comparator = { a, b -> a.id == b.id && a.featureId == b.featureId },
            insert = { classDao.insertClassFeatureCrossRef(it) },
            delete = { classDao.removeClassFeatureCrossRef(it) },
            post = { classService.insertClassFeatureCrossRef(
                featureId = it.featureId,
                id = it.id
            )}
        )

        syncTable(
            serverList = pullSyncService.classSpellTable(),
            localList = classDao.classSpellTable(),
            comparator = { a, b -> a.spellId == b.spellId && a.classId == b.classId },
            insert = { classDao.insertClassSpellCrossRef(it) },
            delete = { classDao.removeClassSpellCrossRef(it) },
            post = { spellService.addClassSpellCrossRef(
                classId = it.classId,
                spellId = it.spellId
            )}
        )

        syncTable(
            serverList = pullSyncService.classSubclassTable(),
            localList = classDao.classSubclassTable(),
            comparator = { a, b -> a.classId == b.classId && a.subclassId == b.subclassId },
            insert = { classDao.insertClassSubclassId(it) },
            delete = { classDao.removeClassSubclassCrossRef(it) },
            post = { classService.insertClassSubclassId(
                classId = it.classId,
                subclassId = it.subclassId
            )}
        )

        syncTable(
            serverList = pullSyncService.featChoiceChoiceTable(),
            localList = characterDao.featChoiceChoiceTable(),
            comparator = { a, b ->
                a.characterId == b.characterId &&
                        a.featId == b.featId &&
                        a.choiceId == b.choiceId
            },
            insert = { featDao.insertFeatChoiceChoiceEntity(it.asTable()) },
            delete = { },
            post = {
                // TODO
            }
        )

        syncTable(
            serverList = pullSyncService.featChoiceFeatTable(),
            localList = featDao.featChoiceFeatTable(),
            comparator = { a, b -> a.featId == b.featId && a.featChoiceId == b.featChoiceId },
            insert = { featDao.insertFeatChoiceFeatCrossRef(it) },
            delete = {

            },
            post = {
                // TODO
            }
        )

        syncTable(
            serverList = pullSyncService.featFeatureTable(),
            localList = featDao.featFeatureTable(),
            comparator = { a, b -> a.featId == b.featId && a.featureId == b.featureId },
            insert = { featDao.insertFeatFeatureCrossRef(it) },
            delete = { },
            post = {
                // TODO
            }
        )

        syncTable(
            serverList = pullSyncService.featureChoiceChoiceTable(),
            localList = characterDao.featureChoiceChoiceTable(),
            comparator = { a, b ->
                a.characterId == b.characterId &&
                        a.featureId == b.featureId &&
                        a.choiceId == b.choiceId
            },
            insert = { characterDao.insertFeatureChoiceEntity(it.asTable()) },
            delete = { },
            post = { characterService.insertFeatureChoiceEntity(
                characterId = it.characterId,
                choiceId = it.choiceId,
                featureId = it.featureId
            )}
        )

        syncTable(
            serverList = pullSyncService.featureOptionsTable(),
            localList = featureDao.featureOptionsTable(),
            comparator = { a, b -> a.id == b.id && a.featureId == b.featureId },
            insert = { featureDao.insertFeatureOptionsCrossRef(it) },
            delete = { featureDao.removeFeatureOptionsCrossRef(it) },
            post = { featureService.insertFeatureOptionsCrossRef(
                featureId = it.featureId,
                id = it.id
            )}
        )

        syncTable(
            serverList = pullSyncService.featureSpellTable(),
            localList = featureDao.featureSpellTable(),
            comparator = { a, b -> a.featureId == b.featureId && a.spellId == b.spellId },
            insert = { featureDao.insertFeatureSpellCrossRef(it) },
            delete = { featureDao.removeFeatureSpellCrossRef(it) },
            post = { featureService.insertFeatureSpellCrossRef(
                spellId = it.spellId,
                featureId = it.featureId
            )}
        )

        syncTable(
            serverList = pullSyncService.optionsFeatureTable(),
            localList = featureDao.optionsFeatureTable(),
            comparator = { a, b -> a.choiceId == b.choiceId && a.featureId == b.featureId },
            insert = { featureDao.insertOptionsFeatureCrossRef(it) },
            delete = { featureDao.removeOptionsFeatureCrossRef(it) },
            post = { featureService.insertOptionsFeatureCrossRef(
                featureId = it.featureId,
                choiceId = it.choiceId
            )}
        )

        syncTable(
            serverList = pullSyncService.pactMagicStateTable(),
            localList = characterDao.pactMagicStateTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.classId == b.classId },
            insert = { characterDao.insertPactMagicStateEntity(it) },
            delete = { },
            post = { characterService.insertPactMagicStateEntity(
                characterId = it.characterId,
                classId = it.classId,
                slotsCurrentAmount = it.slotsCurrentAmount
            )}
        )

        syncTable(
            serverList = pullSyncService.raceChoiceTable(),
            localList = characterDao.raceChoiceTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.raceId == b.raceId },
            insert = { characterDao.insertRaceChoice(it) },
            delete = { },
            post = { characterService.insertRaceChoice(it)}
        )

        syncTable(
            serverList = pullSyncService.raceFeatureTable(),
            localList = raceDao.raceFeatureTable(),
            comparator = { a, b -> a.raceId == b.raceId && a.featureId == b.featureId },
            insert = { raceDao.insertRaceFeatureCrossRef(it) },
            delete = { raceDao.removeRaceFeatureCrossRef(it) },
            post = { raceService.insertRaceFeatureCrossRef(
                featureId = it.featureId,
                raceId = it.raceId
            )}
        )

        syncTable(
            serverList = pullSyncService.raceSubraceTable(),
            localList = raceDao.raceSubraceTable(),
            comparator = { a, b -> a.raceId == b.raceId && a.subraceId == b.subraceId },
            insert = { raceDao.insertRaceSubraceCrossRef(it) },
            delete = { subraceDao.removeRaceSubraceCrossRef(it) },
            post = { raceService.insertRaceSubraceCrossRef(
                subraceId = it.subraceId,
                raceId = it.raceId
            )}
        )

        syncTable(
            serverList = pullSyncService.subclassFeatureTable(),
            localList = subclassDao.subclassFeatureTable(),
            comparator = { a, b -> a.subclassId == b.subclassId && a.featureId == b.featureId },
            insert = { subclassDao.insertSubclassFeatureCrossRef(it) },
            delete = { subclassDao.removeSubclassFeatureCrossRef(it) },
            post = { subclassService.insertSubclassFeatureCrossRef(
                subclassId = it.subclassId,
                featureId = it.featureId
            ) }
        )

        syncTable(
            serverList = pullSyncService.subclassSpellCastingTable(),
            localList = characterDao.subclassSpellCastingTable(),
            comparator = { a, b ->
                a.characterId == b.characterId &&
                        a.spellId == b.spellId &&
                        a.subclassId == b.subclassId
            },
            insert = { characterDao.insertSubClassSpellCastingCrossRef(it) },
            delete = { },
            post = { characterService.insertSubClassSpellCastingCrossRef(
                subclassId = it.subclassId,
                spellId = it.spellId,
                characterId = it.characterId,
                prepared = it.isPrepared
            )}
        )

        syncTable(
            serverList = pullSyncService.subclassSpellTable(),
            localList = subclassDao.subclassSpellTable(),
            comparator = { a, b -> a.subclassId == b.subclassId && a.spellId == b.spellId },
            insert = { subclassDao.insertSubclassSpellCrossRef(it) },
            delete = { subclassDao.removeSubclassSpellCrossRef(it) },
            post = {
                //TODO
            }
        )

        syncTable(
            serverList = pullSyncService.subraceChoiceTable(),
            localList = characterDao.subraceChoiceTable(),
            comparator = { a, b -> a.characterId == b.characterId && a.subraceId == b.subraceId },
            insert = { characterDao.insertSubraceChoiceEntity(it) },
            delete = { characterDao.removeSubraceChoiceEntity(it.characterId, it.subraceId) },
            post = { characterService.insertSubraceChoiceEntity(
                subraceChoiceEntity = it
            )}
        )

        syncTable(
            serverList = pullSyncService.subraceFeatChoiceTable(),
            localList = subraceDao.subraceFeatChoiceTable(),
            comparator = { a, b -> a.featChoiceId == b.featChoiceId && a.subraceId == b.subraceId },
            insert = { subraceDao.insertSubraceFeatChoiceCrossRef(it) },
            delete = { },
            post = {
                // TODO
            }
        )

        syncTable(
            serverList = pullSyncService.subraceFeatureTable(),
            localList = subraceDao.subraceFeatureTable(),
            comparator = { a, b -> a.subraceId == b.subraceId && a.featureId == b.featureId },
            insert = { subraceDao.insertSubraceFeatureCrossRef(it) },
            delete = { subraceDao.removeSubraceFeatureCrossRef(it) },
            post = { subraceService.insertSubraceFeatureCrossRef(
                subraceId = it.subraceId,
                featureId = it.featureId
            )}
        )

        return Result.success()
    }

    private suspend fun <E> syncTable(
        serverList: List<E>,
        localList: List<E>,
        comparator: (a: E, b: E) -> Boolean,
        insert: suspend (E) -> Unit,
        delete: suspend (E) -> Unit,
        post: suspend (E) -> Unit,
    ) {
        serverList.forEach {
            insert(it)
        }

        localList.forEach {
            if (serverList.firstOrNull { item -> comparator(item, it) } == null) {
                // If the entity is missing from the server list either delete it from local or post it to the server to maintain parity.
                if(shouldPost) post(it) else delete(it)
            }
        }
    }
}

