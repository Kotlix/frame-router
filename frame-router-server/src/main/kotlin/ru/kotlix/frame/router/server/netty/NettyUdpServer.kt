package ru.kotlix.frame.router.server.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.server.config.props.NettyProperties

@Component
class NettyUdpServer(
    private val nettyProperties: NettyProperties,
    private val channelInitializer: DatagramPipeline,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var channel: Channel
    private lateinit var serverThread: Thread
    private var serverClosed = false

    @EventListener
    fun contextStartup(event: ContextRefreshedEvent) {
        val bossGroup = NioEventLoopGroup(nettyProperties.workerCount)

        val serverBootstrap =
            Bootstrap()
                .group(bossGroup)
                .channel(NioDatagramChannel::class.java)
                .handler(channelInitializer)
        val serverPort = nettyProperties.port

        val serverChannelFuture = serverBootstrap.bind(serverPort).sync()
        channel = serverChannelFuture.channel()
        logger.info("Netty server started on port $serverPort.")

        if (!::serverThread.isInitialized) {
            serverThread =
                Thread {
                    try {
                        serverChannelFuture.channel().closeFuture().sync()
                    } catch (ex: Exception) {
                        logger.error("Error happened during netty server startup.", ex)
                    } finally {
                        bossGroup.shutdownGracefully()
                        logger.info("Netty server stopped.")
                    }
                }.apply {
                    start()
                }
        }
    }

    @EventListener
    fun contextShutdown(event: ContextClosedEvent) {
        if (!serverClosed) {
            try {
                channel.close()
            } catch (ex: Exception) {
                logger.error("Error happened during netty server shutdown.", ex)
            }
            serverClosed = true
        }
    }
}
