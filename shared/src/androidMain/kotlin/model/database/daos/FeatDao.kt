package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.*

@Dao
actual abstract class FeatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeat(feat: FeatEntityTable): Long

    fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Int {
        val id = insertFeatChoiceOrIgnore(featChoiceEntity.asTable()).toInt()
        if(id == -1) {
            updateFeatChoice(featChoiceEntity.asTable())
            return featChoiceEntity.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatChoiceOrIgnore(featChoiceEntity: FeatChoiceEntityTable): Long

    @Update
    protected abstract fun updateFeatChoice(featChoiceEntity: FeatChoiceEntityTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatChoiceFeatCrossRef(featChoiceFeatCrossRef: FeatChoiceFeatCrossRef)

    @Insert
    abstract fun insertFeatChoiceChoiceEntity(featChoiceChoiceEntity: FeatChoiceChoiceEntityTable)

    @Query("SELECT * FROM feats")
    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>

    @Insert
    abstract fun insertFeatFeatureCrossRef(featFeatureCrossRef: FeatFeatureCrossRef)

    @Query("""SELECT features.* FROM features
JOIN FeatFeatureCrossRef ON FeatFeatureCrossRef.featureId IS features.featureId
WHERE featId IS :featId
    """)
    actual abstract fun getFeatFeatures(featId: Int) : List<Feature>
}