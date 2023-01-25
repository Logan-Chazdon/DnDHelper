package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.model.FeatChoiceEntity
import gmail.loganchazdon.dndhelper.model.FeatEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatChoiceFeatCrossRef

@Dao
abstract class FeatDao {
    @Insert
    abstract fun insertFeat(feat: FeatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatChoiceFeatCrossRef(featChoiceFeatCrossRef: FeatChoiceFeatCrossRef)

    @Insert
    abstract fun insertFeatChoiceChoiceEntity(featChoiceChoiceEntity: FeatChoiceChoiceEntity)

    @Query("SELECT * FROM feats")
    abstract fun getAllFeats(): LiveData<List<Feat>> //TODO impl
}