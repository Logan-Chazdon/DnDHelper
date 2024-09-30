package gmail.loganchazdon.dndhelper

import SharedModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class MyApplication : Application() {
    //lateinit var localDataSource: LocalDataSource
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(AppModule().module)
            modules(SharedModule().module)
        }
    }
}
