package gmail.loganchazdon.dndhelper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var localDataSource: LocalDataSource
}
