package soy.aguilera.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import soy.aguilera.fediverse.FediThread
import java.nio.file.Path

fun Application.configureTemplating( fediThread: FediThread) {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("static")
    }
    routing {
        static("/css") {
            resources("static/css")
        }
        get("/") {
            call.respond(MustacheContent("index.hbs", mapOf(
                "thread" to fediThread
            )))
        }
        get("/publish") {
            try {
                fediThread.publish()
                call.respond("Done")
            }catch( e : Exception){
                call.respond(e.message.toString())
            }
        }
    }
}
