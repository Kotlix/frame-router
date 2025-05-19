package ru.kotlix.frame.router.server.service

import java.net.InetSocketAddress

interface ChannelRegistry {
    fun get(
        channelId: Long,
        userId: Int,
    ): Entry?

    fun get(channelId: Long): List<Pair<Int, InetSocketAddress?>>

    fun subscribe(
        channelId: Long,
        userId: Int,
    )

    fun unsubscribe(
        channelId: Long,
        userId: Int,
    )

    fun add(channelId: Long)

    fun remove(channelId: Long)

    data class Entry(
        var lastAddress: InetSocketAddress?,
    )
}
