package services

import io.ktor.client.*
import model.*
import model.choiceEntities.*
import org.koin.java.KoinJavaComponent.get

private const val PATH = "sync"


class PullSyncService : Service(client = get(HttpClient::class.java)) {
    suspend fun characterTable(): List<CharacterEntity> = getDeserialized("$PATH/characterTable")
    suspend fun backgroundTable(): List<BackgroundEntity> = getDeserialized("$PATH/backgroundTable")
    suspend fun classTable(): List<ClassEntity> = getDeserialized("$PATH/classTable")
    suspend fun featureTable(): List<FeatureEntity> = getDeserialized("$PATH/featureTable")
    suspend fun raceTable(): List<RaceEntity> = getDeserialized("$PATH/raceTable")
    suspend fun spellTable(): List<Spell> = getDeserialized("$PATH/spellTable")
    suspend fun subclassTable(): List<SubclassEntity> = getDeserialized("$PATH/subclassTable")
    suspend fun subraceTable(): List<SubraceEntity> = getDeserialized("$PATH/subraceTable")


    suspend fun characterBackgroundTable(): List<CharacterBackgroundCrossRef> = getDeserialized("$PATH/characterBackgroundTable")
    suspend fun backgroundChoiceTable(): List<BackgroundChoiceEntity> = getDeserialized("$PATH/backgroundChoiceTable")
    suspend fun backgroundFeatureTable(): List<BackgroundFeatureCrossRef> = getDeserialized("$PATH/backgroundFeatureTable")
    suspend fun backgroundSpellTable(): List<BackgroundSpellCrossRef> = getDeserialized("$PATH/backgroundSpellTable")
    suspend fun characterClassTable(): List<CharacterClassCrossRef> = getDeserialized("$PATH/characterClassTable")
    suspend fun characterClassSpellTable(): List<CharacterClassSpellCrossRef> = getDeserialized("$PATH/characterClassSpellTable")
    suspend fun characterFeatureStateTable(): List<CharacterFeatureState> = getDeserialized("$PATH/characterFeatureStateTable")
    suspend fun characterRaceTable(): List<CharacterRaceCrossRef> = getDeserialized("$PATH/characterRaceTable")
    suspend fun characterSubclassTable(): List<CharacterSubclassCrossRef> = getDeserialized("$PATH/characterSubclassTable")
    suspend fun characterSubraceTable(): List<CharacterSubraceCrossRef> = getDeserialized("$PATH/characterSubraceTable")
    suspend fun classChoiceTable(): List<ClassChoiceEntity> = getDeserialized("$PATH/classChoiceTable")
    suspend fun classFeatTable(): List<ClassFeatCrossRef> = getDeserialized("$PATH/classFeatTable")
    suspend fun classFeatureTable(): List<ClassFeatureCrossRef> = getDeserialized("$PATH/classFeatureTable")
    suspend fun classSpellTable(): List<ClassSpellCrossRef> = getDeserialized("$PATH/classSpellTable")
    suspend fun classSubclassTable(): List<ClassSubclassCrossRef> = getDeserialized("$PATH/classSubclassTable")
    suspend fun featChoiceChoiceTable(): List<FeatChoiceChoiceEntity> = getDeserialized("$PATH/featChoiceChoiceTable")
    suspend fun featChoiceFeatTable(): List<FeatChoiceFeatCrossRef> = getDeserialized("$PATH/featChoiceFeatTable")
    suspend fun featFeatureTable(): List<FeatFeatureCrossRef> = getDeserialized("$PATH/featFeatureTable")
    suspend fun featureChoiceChoiceTable(): List<FeatureChoiceChoiceEntity> = getDeserialized("$PATH/featureChoiceChoiceTable")
    suspend fun featureOptionsTable(): List<FeatureOptionsCrossRef> = getDeserialized("$PATH/featureOptionsTable")
    suspend fun featureSpellTable(): List<FeatureSpellCrossRef> = getDeserialized("$PATH/featureSpellTable")
    suspend fun optionsFeatureTable(): List<OptionsFeatureCrossRef> = getDeserialized("$PATH/optionsFeatureTable")
    suspend fun pactMagicStateTable(): List<PactMagicStateEntity> = getDeserialized("$PATH/pactMagicStateTable")
    suspend fun raceChoiceTable(): List<RaceChoiceEntity> = getDeserialized("$PATH/raceChoiceTable")
    suspend fun raceFeatureTable(): List<RaceFeatureCrossRef> = getDeserialized("$PATH/raceFeatureTable")
    suspend fun raceSubraceTable(): List<RaceSubraceCrossRef> = getDeserialized("$PATH/raceSubraceTable")
    suspend fun subclassFeatureTable(): List<SubclassFeatureCrossRef> = getDeserialized("$PATH/subclassFeatureTable")
    suspend fun subclassSpellCastingTable(): List<SubclassSpellCastingSpellCrossRef> = getDeserialized("$PATH/subclassSpellCastingTable")
    suspend fun subclassSpellTable(): List<SubclassSpellCrossRef> = getDeserialized("$PATH/subclassSpellTable")
    suspend fun subraceChoiceTable(): List<SubraceChoiceEntity> = getDeserialized("$PATH/subraceChoiceTable")
    suspend fun subraceFeatChoiceTable(): List<SubraceFeatChoiceCrossRef> = getDeserialized("$PATH/subraceFeatChoiceTable")
    suspend fun subraceFeatureTable(): List<SubraceFeatureCrossRef> = getDeserialized("$PATH/subraceFeatureTable")
}