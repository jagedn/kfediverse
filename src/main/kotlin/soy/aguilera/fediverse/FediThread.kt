package soy.aguilera.fediverse

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.*

class FediThread(private val path: Path, private val instance: String, private val token: String) {

    val title: String
        get() {
            return path.name
        }

    @OptIn(ExperimentalPathApi::class)
    val toots: List<Toot>
        get() {
            var list = Files.walk(path, 1)
                .filter { Files.isDirectory(it) }
                .map {
                    Toot(it)
                }
                .sorted { o1, o2 -> o1.name.compareTo(o2.name) }
                .collect(Collectors.toList())
            return list
        }

    suspend fun publish(): Boolean {
        val client = HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        var parent : String = "";

        toots.forEach {
            parent = publishToot(client, it, parent)
        }

        return true;
    }

    private suspend fun publishToot(client: HttpClient, toot:Toot, parent:String) : String{
        val images : ArrayList<String> = ArrayList()
        toot.images.forEach{tootImage->
            val response = client.submitFormWithBinaryData(
                url = "https://$instance/api/v1/media",
                formData = formData {
                    append("file", tootImage.path.toFile().readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"${tootImage.path.name}\"")
                    })
                },
                block = {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            )
            val tmp : ImageResponse= response.body()
            images.add(tmp.id)
        }

        println("Sending ${toot.body} con parent $parent")
        val response : StatusResponse = client.post("https://$instance/api/v1/statuses"){
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody( TootRequest(toot.body, images.toSet(), parent) )
        }.body()

        return response.id
    }
}
