package gmail.loganchazdon.dndhelper

import gmail.loganchazdon.dndhelper.model.database.configureDatabases
import gmail.loganchazdon.dndhelper.plugins.configureRouting
import io.ktor.client.utils.*
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
        allowHeader("USER_SESSION")
        allowHeader("Access-Control-Allow-Origin")
        exposeHeader("USER_SESSION")
        exposeHeader("Access-Control-Allow-Origin")




        allowHost("0.0.0.0:8081")
        allowHost("127.0.0.1:8081")
        allowHost("localhost:8081")
        allowHost("dndhelper.com:8081")
        allowHost("${System.getenv("domain")}:8081")


        allowCredentials = true
        allowNonSimpleContentTypes = true

         buildHeaders {
             append("Access-Control-Allow-Origin", "https://${System.getenv("domain")}:8081")
             build()
         }
        allowHeader(HttpHeaders.AuthenticationInfo)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Connection)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Cookie)
        allowHeader(HttpHeaders.WWWAuthenticate)

        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowMethods)
        allowHeader(HttpHeaders.AccessControlAllowCredentials)

        exposeHeader(HttpHeaders.AccessControlAllowCredentials)
        exposeHeader(HttpHeaders.AccessControlAllowHeaders)
        exposeHeader(HttpHeaders.AccessControlAllowMethods)
        exposeHeader(HttpHeaders.AccessControlAllowOrigin)


        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)

        allowXHttpMethodOverride()
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
    configureRouting()
    configureDatabases()
}
