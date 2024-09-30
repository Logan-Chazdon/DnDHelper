package gmail.loganchazdon.dndhelper

import gmail.loganchazdon.dndhelper.model.database.configureDatabases
import gmail.loganchazdon.dndhelper.plugins.configureRouting
import io.ktor.server.application.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    configureDatabases()
}
