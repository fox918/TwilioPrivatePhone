import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.fromOptionalFile
import com.twilio.twiml.VoiceResponse
import com.twilio.twiml.voice.Dial
import com.twilio.twiml.voice.Number
import com.twilio.twiml.voice.Sip
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    val mainNumber = Key("phone.mainNumber", stringType)
    val backupNumber = Key("phone.backupNumber", stringType)
    val sipClientUrl = Key("phone.sipClientUrl", stringType)

    val config = EnvironmentVariables() overriding
            fromOptionalFile(File("/etc/simplephone.properties")) overriding
            fromOptionalFile(File("custom.properties")) overriding
            ConfigurationProperties.fromResource("defaults.properties")

    embeddedServer(Netty, port = 8080) {
        install(DefaultHeaders)
        routing {
            get("/health") {
                call.respond(HttpStatusCode.Found)
            }

            get("/outgoing/voice") {
                val dial = Dial.Builder("{{#e164}}{{To}}{{/e164}}").callerId(config[mainNumber]).build()
                val response = VoiceResponse.Builder().dial(dial).build()
                call.respondText(response.toXml(), contentType = ContentType.Text.Xml)
            }

            get("/incoming/voice") {
                val sip = Sip.Builder(config[sipClientUrl]).build()
                val number = Number.Builder(config[backupNumber]).build()
                val dialBoth =
                    Dial.Builder().sip(sip).number(number).callerId(config[mainNumber]).answerOnBridge(true).build()
                val response = VoiceResponse.Builder()
                    .dial(dialBoth)
                    .build()
                call.respondText(response.toXml(), contentType = ContentType.Text.Xml)
            }
        }
    }.start(wait = true)
}


