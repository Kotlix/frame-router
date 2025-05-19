package ru.kotlix.frame.router.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.VoidDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import ru.kotlix.frame.router.api.kafka.VoiceNotification
import ru.kotlix.frame.router.server.config.props.KafkaAuthProperties
import ru.kotlix.frame.router.server.config.props.RouterIdentifier

@EnableKafka
@Configuration
class KafkaConfig {
    @Bean
    fun consumerFactory(
        kafkaProperties: KafkaProperties,
        kafkaAuthProperties: KafkaAuthProperties,
        objectMapper: ObjectMapper,
        routerIdentifier: RouterIdentifier,
    ): ConsumerFactory<Void, VoiceNotification> =
        DefaultKafkaConsumerFactory<Void, VoiceNotification>(
            HashMap<String, Any>()
                .withEntry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers)
                .withEntry(ConsumerConfig.GROUP_ID_CONFIG to routerIdentifier.getServerIdentifier())
                .withEntry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to VoidDeserializer::class.java)
                .withEntry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SASL_PLAINTEXT")
                .withEntry(SaslConfigs.SASL_MECHANISM to "PLAIN")
                .withEntry(SaslConfigs.SASL_JAAS_CONFIG to saslJaasConfig(kafkaAuthProperties)),
        ).apply {
            valueDeserializer = JsonDeserializer(VoiceNotification::class.java, objectMapper)
        }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<Void, VoiceNotification>,
    ): ConcurrentKafkaListenerContainerFactory<Void, VoiceNotification> =
        ConcurrentKafkaListenerContainerFactory<Void, VoiceNotification>().apply {
            this.consumerFactory = consumerFactory
        }

    private fun saslJaasConfig(kafkaAuth: KafkaAuthProperties) =
        "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"${kafkaAuth.username}\" " +
            "password=\"${kafkaAuth.password}\" " +
            "user_${kafkaAuth.username}=\"${kafkaAuth.password}\";"

    private fun <K, V> MutableMap<K, V>.withEntry(entry: Pair<K, V>) =
        this.apply {
            this[entry.first] = entry.second
        }
}
