package ru.kotlix.frame.router.server.handler

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.proto.RoutingContract

@Component
@ChannelHandler.Sharable
class RtcPacketHandler : SimpleChannelInboundHandler<RoutingContract.RtcPacket>() {

    override fun channelRead0(
        ctx: ChannelHandlerContext?,
        msg: RoutingContract.RtcPacket?
    ) {
        TODO("Not yet implemented")
    }
}