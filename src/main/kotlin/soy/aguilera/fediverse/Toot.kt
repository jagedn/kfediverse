package soy.aguilera.fediverse

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.*

class Toot(private val path:Path){

    val name:String
        get() {
            return path.absolutePathString()
        }

    @OptIn(ExperimentalPathApi::class)
    val body:String
        get() {
            val txt = Files.walk(path, 1)
                .filter { !Files.isDirectory(it) && it.name.endsWith(".md") }.findFirst()
            if( txt.isEmpty )
                return ""
            var html = txt.get().readText()
            return html
        }

    val bodyHtml:String
        get() {
            val txt = Files.walk(path, 1)
                .filter { !Files.isDirectory(it) && it.name.endsWith(".md") }.findFirst()
            if( txt.isEmpty )
                return "(no hay texto, se enviará un toot vacio)"
            var html = txt.get().readText()
            html = html.replace("\n","<br/>")
            return html
        }

    @OptIn(ExperimentalPathApi::class)
    val images: Set<TootImage>
        get() {
            val images = Files.walk(path, 1)
                .filter { !Files.isDirectory(it) &&
                        it.name.lowercase(Locale.getDefault()).endsWith(".png") ||
                        it.name.lowercase(Locale.getDefault()).endsWith(".jpg") ||
                        it.name.lowercase(Locale.getDefault()).endsWith(".gif")
            }
            return images.map { f-> TootImage(f) }.collect(Collectors.toSet())
        }

    val hasImages: Boolean
        get() = images.size > 0
}
