package ru.kotlix.frame.router.server.service

interface ChannelService {
    fun startServe(
        channelId: Long,
        shadowId: Int,
    )

    fun stopServe(
        channelId: Long,
        shadowId: Int,
    )
}
