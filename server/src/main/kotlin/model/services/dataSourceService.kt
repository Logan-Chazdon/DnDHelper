package gmail.loganchazdon.dndhelper.model.services

import gmail.loganchazdon.dndhelper.model.dataSources.ServerDataSource
import gmail.loganchazdon.dndhelper.model.database.gson
import io.ktor.client.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val PATH = "datasource"

private fun <T> Routing.listGetter(
    path: String,
    data: () -> List<T>,
    serializer: (List<T>) -> String = { gson.toJson(it) }
) {
    get("$PATH/$path") {
        call.respondText(serializer(data()))
    }
}

fun Routing.dataSourceService(dataSource: ServerDataSource, httpClient: HttpClient) {
    //Items
    listGetter(
        path = "martialWeapons",
        data = { dataSource.martialWeapons },
    )

    listGetter(
        path = "simpleWeapons",
        data = { dataSource.simpleWeapons },
    )

    listGetter(
        path = "armors",
        data = { dataSource.armors },
    )

    listGetter(
        path = "miscItems",
        data = { dataSource.miscItems },
    )


    //Features
    listGetter(
        path = "invocations",
        data = { dataSource.invocations },
    )


    //Misc
    listGetter(
        path = "infusions",
        data = { dataSource.infusions },
    )

    listGetter(
        path = "languages",
        data = { dataSource.languages },
    )

    listGetter(
        path = "metamagics",
        data = { dataSource.metamagics },
    )

    listGetter(
        path = "abilitiesToSkills",
        data = { dataSource.abilitiesToSkills.toList() },
    )
}