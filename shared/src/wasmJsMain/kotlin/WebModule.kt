import model.database.daos.*
import model.localDataSources.DataSource
import model.localDataSources.WebDataSourceImpl
import org.koin.core.annotation.Module
import org.koin.dsl.module


@Module
class WebModule {
    val module = module {
        single <CharacterDao> { CharacterDaoImpl(get()) }
        single <BackgroundDao> { BackgroundDaoImpl(get()) }
        single <ClassDao> { ClassDaoImpl(get()) }
        single <FeatDao> { FeatDaoImpl(get()) }
        single <FeatureDao> { FeatureDaoImpl(get()) }
        single <RaceDao> { RaceDaoImpl(get()) }
        single <SpellDao> { SpellDaoImpl(get()) }
        single <SubclassDao> { SubclassDaoImpl(get()) }
        single <SubraceDao> { SubraceDaoImpl(get()) }

        single <DataSource> { WebDataSourceImpl() }
    }
}