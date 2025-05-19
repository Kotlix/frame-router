package ru.kotlix.frame.router.server.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "netty.server")
data class NettyProperties(
    var port: Int,
    var workerCount: Int = 2,
)
