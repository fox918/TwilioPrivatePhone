import com.natpryce.konfig.*
import com.twilio.twiml.VoiceResponse
import com.twilio.twiml.voice.Dial
import com.twilio.twiml.voice.Sip
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File

fun main() {
    val mainNumber = Key("phone.mainNumber", stringType)
    val sipClientUrl = Key("phone.sipClientUrl", stringType)

    val config = EnvironmentVariables() overriding
            ConfigurationProperties.fromFile(File("/etc/simplephone.properties")) overriding
            ConfigurationProperties.fromResource("custom.properties") overriding
            ConfigurationProperties.fromResource("defaults.properties")




    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/outgoing/voice") {
                val dial = Dial.Builder("{{#e164}}{{To}}{{/e164}}").callerId(config[mainNumber]).build()
                val response = VoiceResponse.Builder().dial(dial).build()
                call.respondText(response.toXml(), contentType = ContentType.Text.Xml)
            }

            get("/incoming/voice") {
                val sip = Sip.Builder(config[sipClientUrl]).build();
                val dial = Dial.Builder().sip(sip).answerOnBridge(true).build()
                val respone = VoiceResponse.Builder().dial(dial).build()
                call.respondText(respone.toXml(), contentType = ContentType.Text.Xml)
            }
        }
    }.start(wait = true)
}


