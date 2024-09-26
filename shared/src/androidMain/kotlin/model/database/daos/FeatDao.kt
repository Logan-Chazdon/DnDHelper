package model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import model.Feat
import model.FeatChoiceEntity
import model.FeatEntity
import model.Feature
import model.choiceEntities.FeatChoiceChoiceEntity
import model.junctionEntities.FeatChoiceFeatCrossRef
import model.junctionEntities.FeatFeatureCrossRef

@Dao
abstract class FeatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeat(feat: FeatEntity): Long

    fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Int {
        val id = insertFeatChoiceOrIgnore(featChoiceEntity).toInt()
        if(id == -1) {
            updateFeatChoice(featChoiceEntity)
            return featChoiceEntity.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatChoiceOrIgnore(featChoiceEntity: FeatChoiceEntity): Long

    @Update
    protected abstract fun updateFeatChoice(featChoiceEntity: FeatChoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatChoiceFeatCrossRef(featChoiceFeatCrossRef: FeatChoiceFeatCrossRef)

    @Insert
    abstract fun insertFeatChoiceChoiceEntity(featChoiceChoiceEntity: FeatChoiceChoiceEntity)

    @Query("SELECT * FROM feats")
    abstract fun getUnfilledFeats(): LiveData<List<Feat>>

    @Insert
    abstract fun insertFeatFeatureCrossRef(featFeatureCrossRef: FeatFeatureCrossRef)

    @Query("""SELECT features.* FROM features
JOIN FeatFeatureCrossRef ON FeatFeatureCrossRef.featureId IS features.featureId
WHERE featId IS :featId
    """)
    abstract fun getFeatFeatures(featId: Int) : List<Feature>
}