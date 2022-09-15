package uz.devcraft.artemis

import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.jms.support.converter.SimpleMessageConverter
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import javax.annotation.PostConstruct
import javax.jms.Message
import javax.jms.Session

@Service
@EnableJms
class ArtemisService(
    private val template: JmsTemplate
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @JmsListener(destination = "TestDestination2")
    fun sendReceive(message: Person) {
        log.info("received message 2: $message")
    }


    @JmsListener(destination = "TestDestination")
    fun sendReceive2(message: Message) {
        log.info("received message: ${message.getBody(String::class.java)}")
        template.send(message.jmsReplyTo, CreateMessage("received", message.jmsCorrelationID))
    }

    @PostConstruct
    fun sendMessage() {
        val future = CompletableFuture<Message>().completeAsync {
            template.sendAndReceive("TestDestination", CreateMessage("Test send and receive", "request_q"))
        }.thenAccept {
            val converter = SimpleMessageConverter()
            log.info("Send and received message: ${converter.fromMessage(it!!)}")
        }

        template.convertAndSend("TestDestination2", "Hello ARTEMIS")
    }


}

data class Person(
    var name: String? = null
)


class CreateMessage(private var text: String? = null, private var agentRequestId: String? = null) : MessageCreator {
    override fun createMessage(session: Session): Message {
        val message = session.createTextMessage(text)
        message.jmsCorrelationID = agentRequestId
        return message
    }
}

enum class MyEnum {
    FF1, FF2
}