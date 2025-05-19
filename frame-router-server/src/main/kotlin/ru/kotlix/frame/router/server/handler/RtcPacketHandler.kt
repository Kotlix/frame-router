package ru.kotlix.frame.router.server.handler

import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.proto.RoutingContract
import ru.kotlix.frame.router.server.handler.SenderExtractor.Companion.SENDER_KEY
import ru.kotlix.frame.router.server.service.ChannelRegistry

@Component
@ChannelHandler.Sharable
class RtcPacketHandler(
    private val channelRegistry: ChannelRegistry,
) : SimpleChannelInboundHandler<RoutingContract.RtcPacket>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun channelRead0(
        ctx: ChannelHandlerContext?,
        msg: RoutingContract.RtcPacket?,
    ) {
        val context = ctx ?: return
        val packet = msg ?: return

        val entry = channelRegistry.get(packet.channelId, packet.shadowId)
        if (entry == null) {
            logger.warn("Received packet for not serving channel ${packet.channelId} with shadow ${packet.shadowId}")
            return
        }

        cacheSenderAddress(context, entry)
        if (packet.hasWave()) {
            logger.debug("Received wave packet.")
            routeWavePacket(context, packet)
        } else {
            logger.debug("Received ping packet.")
        }
    }

    private fun cacheSenderAddress(
        context: ChannelHandlerContext,
        entry: ChannelRegistry.Entry,
    ) {
        val sender = context.channel().attr(SENDER_KEY).get()
        if (sender == null) {
            logger.error("Datagram sender not provided.")
            return
        }
        entry.lastAddress = sender
    }

    private fun routeWavePacket(
        context: ChannelHandlerContext,
        packet: RoutingContract.RtcPacket,
    ) {
        channelRegistry.get(packet.channelId).forEach { (_, v) ->
            val recipient = v ?: return@forEach
            val encodedPacket = encodeProto(packet)
            if (encodedPacket == null) {
                logger.error("Unable to encode proto.")
            }
            context.write(DatagramPacket(encodedPacket, recipient))
        }
        context.flush()
    }

    private fun encodeProto(packet: Any): ByteBuf? {
        if (packet is MessageLite) {
            return Unpooled.wrappedBuffer(packet.toByteArray())
        } else if (packet is MessageLite.Builder) {
            return Unpooled.wrappedBuffer(packet.build().toByteArray())
        }
        return null
    }
}
