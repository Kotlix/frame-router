package ru.kotlix.frame.router.server.service

import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

@Component
class ConcurrentHashChannelRegistry : ChannelRegistry {
    private val map = ConcurrentHashMap<Long, ConcurrentHashMap<Int, ChannelRegistry.Entry>>()

    override fun get(channelId: Long): List<Pair<Int, InetSocketAddress?>> {
        val channel = map[channelId] ?: return emptyList()

        return channel.map { (k, v) -> k to v.lastAddress }
    }

    override fun get(
        channelId: Long,
        userId: Int,
    ): ChannelRegistry.Entry? = map[channelId]?.get(userId)

    override fun subscribe(
        channelId: Long,
        userId: Int,
    ) {
        val channel =
            map[channelId]
                ?: throw IllegalStateException("Channel not exists.")

        channel[userId] = ChannelRegistry.Entry(null)
    }

    override fun unsubscribe(
        channelId: Long,
        userId: Int,
    ) {
        val channel =
            map[channelId]
                ?: throw IllegalStateException("Channel not exists.")

        channel.remove(userId)
    }

    override fun add(channelId: Long) {
        map[channelId] = ConcurrentHashMap<Int, ChannelRegistry.Entry>()
    }

    override fun remove(channelId: Long) {
        map.remove(channelId)
    }
}
