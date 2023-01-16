package soy.aguilera

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import soy.aguilera.fediverse.FediThread
import soy.aguilera.plugins.configureAdministration
import soy.aguilera.plugins.configureTemplating
import java.nio.file.Path
import kotlin.io.path.exists

var fediThread : FediThread? = null;

fun main(args: Array<String>) {

    val parser = ArgParser("kfediverse")

    val input by parser.argument(ArgType.String, description = "Input directory")

    val port by parser.option(ArgType.Int, shortName = "p", description = "port").default(8080)
    val token by parser.option(ArgType.String, shortName = "t", description = "token").required()
    val instance by parser.option(ArgType.String, shortName = "i", description = "instance").required()

    parser.parse(args)

    var path = Path.of(input)
    assert(path!!.exists())

    fediThread = FediThread(path, instance, token)

    embeddedServer(Netty, port = port, host = "127.0.0.1", module = Application::module)
            .start(wait = true)
}

fun Application.module() {
    fediThread?.let { configureTemplating(it) }
    configureAdministration()
}
