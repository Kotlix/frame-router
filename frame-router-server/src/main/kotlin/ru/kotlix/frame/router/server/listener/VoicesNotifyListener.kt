package ru.kotlix.frame.router.server.listener

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.kafka.UpdateInfo
import ru.kotlix.frame.router.api.kafka.VoiceNotification
import ru.kotlix.frame.router.server.config.props.RouterIdentifier
import ru.kotlix.frame.router.server.service.ChannelService
import kotlin.jvm.java

@Component
class VoicesNotifyListener(
    private val channelService: ChannelService,
    private val routerIdentifier: RouterIdentifier,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["\${router.kafka.consumer.topic}"])
    fun onMessage(value: VoiceNotification) {
        logger.info("Received message from topic.")
        if (routerIdentifier.getServerIdentifier() != getServerIdentifier(value)) {
            logger.info("Skipping message: target server differs.")
            return
        }

        try {
            val channelId = value.connectionInfo.channelId
            val shadowId = value.updateInfo.attendant.shadowId
            val action =
                value.updateInfo.action ?: run {
                    logger.warn("Skipping message: received null action.")
                    return
                }

            when (action) {
                UpdateInfo.Action.JOINED -> channelService.startServe(channelId, shadowId)
                UpdateInfo.Action.LEFT -> channelService.stopServe(channelId, shadowId)
            }
        } catch (ex: RuntimeException) {
            logger.error("Error happened during message handle.", ex)
        }
    }

    private fun getServerIdentifier(value: VoiceNotification) = value.connectionInfo.let { "${it.voiceRegion}-${it.voiceName}" }
}
