package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.SubraceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.*

private const val fullCharacterSql =
    """SELECT * FROM characters 
INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
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
    }

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

    //Class Table
    @Query("SELECT * FROM classes")
    abstract fun getAllClasses(): List<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClass(newClass: ClassEntity)

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

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT * FROM features 
JOIN RaceFeatureCrossRef ON raceId IS :raceId 
WHERE RaceFeatureCrossRef.featureId is features.featureId""")
    protected abstract fun getRaceFeatures(raceId: Int) : List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubrace(subrace: Subrace)

    @Query("""SELECT * FROM subraces
JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id
JOIN SubraceFeatureCrossRef ON SubraceFeatureCrossRef.subraceId IS subraces.id
JOIN features ON features.featureId IS SubraceFeatureCrossRef.featureId
WHERE raceId IS :raceId
    """)
    @Transaction
    abstract fun getSubraceOptions(raceId: Int): List<Subrace>

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
    abstract fun insertFeat(feat: Feat)
}