package ru.kotlix.frame.router.server.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.proto.RoutingContract

@Component
class DatagramPipeline(
    private val clientPacketsHandler: SimpleChannelInboundHandler<RoutingContract.RtcPacket>,
) : ChannelInitializer<DatagramChannel>() {

    override fun initChannel(ch: DatagramChannel?) {
        ch?.pipeline()?.apply {
//            addLast(LoggingHandler(LogLevel.DEBUG))

            addLast(ProtobufDecoder(RoutingContract.RtcPacket.getDefaultInstance()))

            addLast(ProtobufEncoder())

            addLast(clientPacketsHandler)
        }
    }
}
