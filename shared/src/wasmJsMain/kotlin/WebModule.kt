import model.database.daos.*
import org.koin.core.annotation.Module
import org.koin.dsl.module


@Module
class WebModule {
    val module = module {
        single <CharacterDao> { CharacterDaoImpl(get()) }
        single <BackgroundDao> { BackgroundDaoImpl() }
        single <ClassDao> { ClassDaoImpl() }
        single <FeatDao> { FeatDaoImpl() }
        single <FeatureDao> { FeatureDaoImpl() }
        single <RaceDao> { RaceDaoImpl() }
        single <SpellDao> { SpellDaoImpl() }
        single <SubclassDao> { SubclassDaoImpl() }
        single <SubraceDao> { SubraceDaoImpl() }
    }
}