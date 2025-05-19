package ru.kotlix.frame.router.server.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.kafka.VoiceNotification
import ru.kotlix.frame.session.api.kafka.MessageNotification
import ru.kotlix.frame.session.server.mapper.toServiceMessageNotification
import ru.kotlix.frame.session.server.service.NotifierService
import kotlin.jvm.java

@Component
class VoicesNotifyListener(
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["\${router.kafka.consumer.topic}"])
    fun onMessage(value: String) {
        logger.info("Received message from topic.")
        try {
            val message = objectMapper.readValue(value, VoiceNotification::class.java)
            logger.debug("Parsed message.")

            // TODO:
        } catch (ex: RuntimeException) {
            logger.error("Error happened during message handle.")
        }
    }
}
