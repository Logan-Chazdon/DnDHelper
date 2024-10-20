package gmail.loganchazdon.dndhelper

import gmail.loganchazdon.dndhelper.model.database.configureDatabases
import gmail.loganchazdon.dndhelper.plugins.configureRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        allowHost("0.0.0.0:8081")
        allowHost("localhost:8081")

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Connection)

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
    configureRouting()
    configureDatabases()
}
