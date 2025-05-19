package ru.kotlix.frame.router.server.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.socket.DatagramPacket
import io.netty.util.AttributeKey
import java.net.InetSocketAddress

class SenderExtractor : ChannelInboundHandlerAdapter() {
    companion object {
        val SENDER_KEY: AttributeKey<InetSocketAddress> = AttributeKey.valueOf("sender")
    }

    override fun channelRead(
        ctx: ChannelHandlerContext?,
        msg: Any?,
    ) {
        if (ctx != null && msg is DatagramPacket) {
            ctx.channel().attr(SENDER_KEY).set(msg.sender())
        }
        ctx?.fireChannelRead(msg)
    }
}
