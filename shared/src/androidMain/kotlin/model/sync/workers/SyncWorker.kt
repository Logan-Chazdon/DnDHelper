package model.sync.workers

import android.util.Log
import androidx.work.CoroutineWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext.get
import org.koin.java.KoinJavaComponent.inject
import services.*


abstract class SyncWorker<T>(
    val type: TypeToken<T>
) : CoroutineWorker(get().get(), get().get()) {
    private val gson : Gson by inject(Gson::class.java)
    protected val characterService: CharacterService by inject(CharacterService::class.java)
    protected val featureService: FeatureService by inject(FeatureService::class.java)
    protected val backgroundService: BackgroundService by inject(BackgroundService::class.java)
    protected val classService: ClassService by inject(ClassService::class.java)
    protected val subclassService: SubclassService by inject(SubclassService::class.java)
    protected val spellService: SpellService by inject(SpellService::class.java)
    protected val raceService: RaceService by inject(RaceService::class.java)
    protected val subraceService: SubraceService by inject(SubraceService::class.java)

    abstract suspend fun sync(it: T)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        println("JsonInput: ${inputData.getString("json")}")
        val entity = gson.fromJson<T>(inputData.getString("json"), type.type)

        try {
            sync(entity)
        } catch (e: Exception) {
            Log.e("SyncWorker", "pushSync: ${e.message}")
            return@withContext Result.retry()
        }
        Result.success()
    }
}