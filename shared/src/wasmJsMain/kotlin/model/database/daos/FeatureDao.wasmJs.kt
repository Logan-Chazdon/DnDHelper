package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell
import services.*

actual abstract class FeatureDao {
    protected val featureService: FeatureService
    protected val backgroundService: BackgroundService
    protected val featService: FeatService
    protected val raceService: RaceService
    protected val classService: ClassService

    constructor(
        featureService: FeatureService,
        featService: FeatService,
        backgroundService: BackgroundService,
        raceService: RaceService,
        classService: ClassService
    ) {
        this.featureService = featureService
        this.featService = featService
        this.backgroundService = backgroundService
        this.raceService = raceService
        this.classService = classService
    }

    actual abstract suspend fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)
    actual abstract suspend fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>
    actual abstract suspend fun getFeatureSpells(featureId: Int): List<Spell>?

    actual suspend fun getFilledBackgroundFeatures(id: Int): List<Feature> {
        return backgroundService.getUnfilledBackgroundFeatures(id)
    }

    actual suspend fun getFilledLevelPath(id: Int): MutableList<Feature> = classService.getFilledLevelPath(id)
    actual suspend fun getFeatFeatures(featId: Int): List<Feature> = featService.getFeatFeatures(featId)
    actual suspend fun getRaceTraits(id: Int): List<Feature> = raceService.getRaceFeatures(id)


    actual suspend fun insertFeature(feature: FeatureEntity): Int {
        return featureService.insertFeature(feature)
    }

    actual abstract fun getLiveFeatureById(id: Int): Flow<Feature>
    actual suspend fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
        featureService.insertFeatureOptionsCrossRef(featureId, id)
    }

    actual suspend fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        return featureService.insertFeatureChoice(option)
    }

    actual suspend fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        featureService.removeFeatureOptionsCrossRef(featureId, id)
    }

    actual suspend fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        featureService.removeOptionsFeatureCrossRef(featureId, choiceId)
    }

    actual abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>
    actual abstract suspend fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>
    actual abstract suspend fun clearFeatureChoiceIndexRefs(id: Int)
    actual suspend fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        featureService.insertFeatureChoiceIndexCrossRef(choiceId, index, levels, classes, schools)
    }

    actual suspend fun insertIndexRef(index: String, ids: List<Int>) {
        featureService.insertIndexRef(index, ids)
    }

    actual suspend fun removeIdFromRef(id: Int, ref: String) {
        featureService.removeIdFromRef(id, ref)
    }

    actual suspend fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        featureService.insertFeatureSpellCrossRef(spellId, featureId)
    }

    actual abstract suspend fun getFeatureIdOr0FromSpellId(id: Int): Int
    actual abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
    actual suspend fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        featureService.removeFeatureSpellCrossRef(spellId, featureId)
    }

    actual abstract fun returnGetAllIndexes(): Flow<List<String>>
    actual suspend fun insertOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        featureService.insertOptionsFeatureCrossRef(featureId, choiceId)
    }

}


class FeatureDaoImpl(
    service: FeatureService, featService: FeatService, backgroundService: BackgroundService,
    raceService: RaceService, classService: ClassService
) : FeatureDao(
    service, featService,
    backgroundService, raceService, classService,
) {
    override suspend fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        featureService.removeFeatureFeatureChoice(choiceId, characterId)
    }

    override suspend fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity> {
        return featureService.getFeatureChoices(featureId)
    }

    override suspend fun getFeatureSpells(featureId: Int): List<Spell>? {
        return featureService.getFeatureSpells(featureId)
    }

    override fun getLiveFeatureById(id: Int): Flow<Feature> {
        return featureService.getLiveFeatureById(id)
    }

    override fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>> {
        return featureService.getLiveFeatureChoices(featureId)
    }

    override suspend fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature> {
        return featureService.getFeatureChoiceOptions(featureChoiceId)
    }

    override suspend fun clearFeatureChoiceIndexRefs(id: Int) {
        featureService.clearFeatureChoiceIndexRefs(id)
    }

    override suspend fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return featureService.getFeatureIdOr0FromSpellId(id)
    }

    override fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?> {
        return featureService.getLiveFeatureSpells(id)
    }

    override fun returnGetAllIndexes(): Flow<List<String>> {
        return featureService.returnGetAllIndexes()
    }

}