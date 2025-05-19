package ru.kotlix.frame.router.server.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.kotlix.frame.router.server.hash.ValueRepresent
import java.net.InetSocketAddress

@Service
class ChannelServiceImpl(
    private val channelRegistry: ChannelRegistry,
    private val valueRepresent: ValueRepresent,
) : ChannelService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun startServe(
        channelId: Long,
        shadowId: Int,
    ) {
        var data = channelRegistry.get(channelId)
        if (data.isEmpty()) {
            channelRegistry.add(channelId)
            logger.info("Adding channel ${valueRepresent.representLong(channelId)}.")
        }
        channelRegistry.subscribe(channelId, shadowId)
        logger.info("Subscribed ${valueRepresent.representInt(shadowId)} to channel ${valueRepresent.representLong(channelId)}.")

        data = channelRegistry.get(channelId)
        logger.info("Channel state: ${represent(channelId, data)}.")
    }

    override fun stopServe(
        channelId: Long,
        shadowId: Int,
    ) {
        var data = channelRegistry.get(channelId)
        if (data.isEmpty()) {
            logger.warn("Skipping: no channel ${valueRepresent.representLong(channelId)}.")
            return
        }
        channelRegistry.unsubscribe(channelId, shadowId)
        logger.info("Unsubscribed ${valueRepresent.representInt(shadowId)} from channel ${valueRepresent.representLong(channelId)}.")

        data = channelRegistry.get(channelId)
        logger.info("Channel state: ${represent(channelId, data)}.")
        if (data.isEmpty()) {
            channelRegistry.remove(channelId)
            logger.info("Removed channel ${valueRepresent.representLong(channelId)}.")
        }
    }

    private fun represent(
        channelId: Long,
        keys: List<Pair<Int, InetSocketAddress?>>,
    ): String {
        val channelStr = valueRepresent.representLong(channelId)
        if (keys.isNotEmpty()) {
            val keysStr =
                keys.joinToString(", ") {
                    "${valueRepresent.representInt(it.first)}=${it.second ?: "unknown"}"
                }
            return "Channel($channelStr) [$keysStr]"
        }
        return "Channel($channelStr) []"
    }
}
