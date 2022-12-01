package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.SubraceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.*

private const val fullCharacterSql =
    """WITH subrace AS (SELECT id AS subraceid, name AS subracename, abilityBonuses AS subraceabilityBonuses, abilityBonusChoice AS subraceabilityBonusChoice, 
startingProficiencies AS subracestartingProficiencies, languages AS subracelanguages, languageChoices AS subracelanguageChoices, size AS subracesize, 
groundSpeed AS subracegroundSpeed FROM subraces)        
SELECT * FROM characters 
INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
INNER JOIN CharacterSubraceCrossRef ON CharacterSubraceCrossRef.characterId IS characters.id
INNER JOIN subrace ON subrace.subraceid IS CharacterSubraceCrossRef.subraceId
WHERE characters.id = :id"""

@Dao
abstract class DatabaseDao {
    //Character Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacter(character: CharacterEntity) : Long

    //Fill out choices which require lists. Cant be done in sql due to lack of support.
    //Does not need to set non list choices.
    private fun fillOutCharacterChoiceLists(character: Character) {
        //Fill out race choices
        character.race?.let { race ->
            getRaceChoiceData(raceId = race.raceId, charId = character.id).let { data ->
                race.proficiencyChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.proficiencyChoice.getOrNull(index) ?: emptyList()
                }

                race.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = getRaceFeatures(race.raceId)
                features.forEach { feature ->
                    feature.choices = fillOutChoices(getFeatureChoices(feature.featureId), characterId = character.id)
                }
                race.traits = features
            }
        }

        character.race?.subrace?.let { subrace ->
            getSubraceChoiceData(subraceId = subrace.id, charId = character.id).let { data ->
                subrace.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = getSubraceFeatures(subrace.id)
                features.forEach { feature ->
                    feature.choices = fillOutChoices(getFeatureChoices(feature.featureId), characterId = character.id)
                }
                subrace.traits = features

                val featChoiceEntities = getSubraceFeatChoices(subrace.id)
                val featChoices = mutableListOf<FeatChoice>()
                featChoiceEntities.forEach {
                    featChoices.add(
                        it.toFeatChoice(getFeatChoiceChosen(characterId = character.id, choiceId = it.id))
                    )
                }
                subrace.featChoices =featChoices
            }
        }
    }

    @Query("""SELECT * FROM feats
JOIN FeatChoiceChoiceEntity ON FeatChoiceChoiceEntity.featId IS feats.id
WHERE choiceId IS :choiceId AND characterId IS :characterId
    """)
    abstract fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>

    @Query("""SELECT * FROM featChoices
JOIN SubraceFeatChoiceCrossRef ON SubraceFeatChoiceCrossRef.featChoiceId IS featChoices.id
WHERE SubraceFeatChoiceCrossRef.subraceId IS :id
    """)
    abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>


    //This only fills out chosen not options.
    //We don't want options as it is not used inside of the character object.
    private fun fillOutChoices(choiceEntities: List<FeatureChoiceEntity>, characterId: Int) : List<FeatureChoice> {
        val choices = mutableListOf<FeatureChoice>()
        choiceEntities.forEach { featureChoiceEntity ->
            val features = getFeatureChoiceChosen(choiceId = featureChoiceEntity.id, characterId = characterId)
            features.forEach {
                it.choices = fillOutChoices(it.choices ?: emptyList(), characterId)
            }
            choices.add(
                FeatureChoice(
                    entity = featureChoiceEntity,
                    options = emptyList(),
                    chosen = features
                )
            )
        }
        return choices
    }

    @RewriteQueriesToDropUnusedColumns
    @Query(fullCharacterSql)
    @Transaction
    protected abstract fun findCharacterWithoutListChoices(id: Int) : Character

    @Query(fullCharacterSql)
    @RewriteQueriesToDropUnusedColumns
    @Transaction
    protected abstract fun findLiveCharacterWithoutListChoices(id: Int) : LiveData<Character>


    @Query("SELECT * FROM RaceChoiceEntity WHERE raceId = :raceId AND characterId = :charId")
    protected abstract fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity

    @Query("SELECT * FROM SubraceChoiceEntity WHERE subraceId = :subraceId AND characterId = :charId")
    abstract fun getSubraceChoiceData(subraceId: Int, charId: Int): SubraceChoiceEntity

    suspend fun findCharacterById(id: Int): Character {
        val character = findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    fun findLiveCharacterById(id: Int, character : MediatorLiveData<Character> ) {
        val characterLiveData  =findLiveCharacterWithoutListChoices(id)
        character.addSource(characterLiveData) {
            fillOutCharacterChoiceLists(it)
            character.value = it
        }
    }

    @Query("DELETE FROM characters WHERE id = :id")
    abstract fun deleteCharacter(id: Int)

    @Transaction
    @Query("SELECT * FROM characters " +
            "INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id\n" +
            "INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId")
    abstract fun getAllCharacters(): LiveData<List<Character>>

    @Insert
    abstract fun insertCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Delete
    abstract fun removeCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceChoice(choice: RaceChoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceEntity(choice : FeatureChoiceChoiceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubRaceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef)

    //Class Table
    @Query("SELECT * FROM classes")
    abstract fun getAllClasses(): List<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClass(newClass: ClassEntity)

    @Insert
    abstract fun insertSubclass(subClass: SubclassEntity)

    //Race Table
    @Query("SELECT * FROM races")
    @Transaction
    abstract fun getAllRaces(): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRace(newRace: RaceEntity) : Long

    @Query("DELETE FROM races WHERE raceId = :id")
    abstract fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    @Transaction
    abstract fun findLiveRaceById(id: Int) : LiveData<Race>

    @Insert
    abstract fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Delete
    abstract fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Insert
    abstract fun insertSubraceFeatChoiceCrossRef(subraceFeatChoiceCrossRef: SubraceFeatChoiceCrossRef)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT * FROM features 
JOIN RaceFeatureCrossRef ON features.featureId IS RaceFeatureCrossRef.featureId 
WHERE raceId is :raceId""")
    protected abstract fun getRaceFeatures(raceId: Int) : List<Feature>


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId is :subraceId""")
    protected abstract fun getSubraceFeatures(subraceId: Int) : List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubrace(subrace: SubraceEntity) : Long

    @Query("""SELECT * FROM subraces
JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id
WHERE raceId IS :raceId
    """)
    @Transaction
    protected abstract fun getSubraceOptionsWithoutFeatures(raceId: Int): List<SubraceEntity>

    @Query("""SELECT * FROM features
JOIN SubraceFeatureCrossRef ON SubraceFeatureCrossRef.featureId IS features.featureId
WHERE subraceId IS :subraceId
    """)
    protected abstract fun getSubraceTraits(subraceId: Int) : List<Feature>

    fun getSubraceOptions(raceId: Int) : List<Subrace> {
        val result : List<Subrace> = getSubraceOptionsWithoutFeatures(raceId) as List<Subrace>
        result.forEach {
            it.traits = getSubraceTraits(it.id)
        }
        return result
    }

    @Insert
    abstract fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Insert
    abstract fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    @Delete
    abstract fun removeSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    //Feature Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeature(feature: FeatureEntity) : Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getLiveFeatureById(id: Int) : LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getFeatureById(id: Int) : Feature

    @Insert
    abstract fun insertFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Delete
    abstract fun removeFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Insert
    abstract fun insertOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Delete
    abstract fun removeOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Insert
    abstract fun insertFeatureChoice(option: FeatureChoiceEntity) : Long

    @Query("DELETE FROM features WHERE featureId = :id")
    abstract fun removeFeatureChoice(id : Int)

    //This returns all featureChoices associate with a feature. It doesn't not contain the options or the chosen fields.
    @Query("""SELECT * FROM FeatureChoiceEntity 
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId""")
    protected abstract fun getFeatureChoices(featureId: Int) : List<FeatureChoiceEntity>

    //This returns all features which belong in the options field of a featureChoice.
    @Query("""SELECT * FROM features
JOIN OptionsFeatureCrossRef ON OptionsFeatureCrossRef.featureId IS features.featureId
WHERE OptionsFeatureCrossRef.choiceId IS :featureChoiceId""")
    protected abstract fun getFeatureChoiceOptions(featureChoiceId: Int) : List<Feature>

    //This returns all features which belong in the chosen field of a featureChoice.
    @Query("""SELECT * FROM features 
JOIN FeatureChoiceChoiceEntity ON features.featureId IS FeatureChoiceChoiceEntity.featureId
WHERE FeatureChoiceChoiceEntity.characterId IS :characterId AND FeatureChoiceChoiceEntity.choiceId IS :choiceId""")
    protected abstract fun getFeatureChoiceChosen(characterId: Int, choiceId: Int) : List<Feature>

    //Feats
    @Insert
    abstract fun insertFeat(feat: FeatEntity) : Long

    @Insert
    abstract fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Long

    @Insert
    abstract fun insertFeatChoiceFeatCrossRef(featChoiceFeatCrossRef: FeatChoiceFeatCrossRef)

    @Insert
    abstract fun insertFeatChoiceChoiceEntity(featChoiceChoiceEntity: FeatChoiceChoiceEntity)
}