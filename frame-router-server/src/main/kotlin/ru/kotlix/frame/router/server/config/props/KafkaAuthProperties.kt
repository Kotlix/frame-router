package ru.kotlix.frame.router.server.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "router.kafka.authentication")
data class KafkaAuthProperties(
    var username: String,
    var password: String,
)
