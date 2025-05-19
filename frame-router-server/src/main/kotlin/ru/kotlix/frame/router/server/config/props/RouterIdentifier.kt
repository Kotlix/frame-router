package ru.kotlix.frame.router.server.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "router.id")
data class RouterIdentifier(
    val region: String,
    val name: String,
) {
    fun getServerIdentifier() = "$region-$name"
}
