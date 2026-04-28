package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.*

@Dao
actual abstract class FeatDao {

    // This is necessary to avoid using OnConflictStrategy.REPlACE as that causes foreign key issues.
    fun insertFeat(feat: FeatEntityTable): Int {
        return if (checkForFeatAtId(feat.id) == true) {
            updateFeat(feat)
        } else {
            insertNewFeat(feat).toInt()
        } ?: 0
    }


    // The return types are weird here. For some reason room throws and error if we use the same type for both.
    @Update
    protected abstract fun updateFeat(feat: FeatEntityTable): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertNewFeat(feat: FeatEntityTable): Long


    @Query("SELECT EXISTS(SELECT 1 FROM feats WHERE id = :id)")
    protected abstract fun checkForFeatAtId(id: Int): Boolean?


    fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Int {
        val id = insertFeatChoiceOrIgnore(featChoiceEntity.asTable()).toInt()
        if (id == -1) {
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


    @Query("SELECT * FROM feats")
    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatFeatureCrossRef(featFeatureCrossRef: FeatFeatureCrossRef)


    @Query("SELECT * FROM FeatChoiceFeatCrossRef")
    abstract fun featChoiceFeatTable(): List<FeatChoiceFeatCrossRef>

    @Query("SELECT * FROM FeatFeatureCrossRef")
    abstract fun featFeatureTable(): List<FeatFeatureCrossRef>
}