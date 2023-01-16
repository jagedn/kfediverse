package soy.aguilera.fediverse

import java.nio.file.Path
import java.util.*
import kotlin.io.path.readBytes

class TootImage(val path: Path){

    val src:String
        get() {
            return "data:image/png;base64,"+Base64.getEncoder().encodeToString(path.readBytes())
        }

}
