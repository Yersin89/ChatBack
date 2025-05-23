package kz.spring.springboot.chat.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import kz.spring.springboot.chat.Entity.Message;

@Service
public class KafkaMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String topic, Message message) {
        try {
            // Сериализуем объект Message в JSON
            String messageJson = objectMapper.writeValueAsString(message);

            kafkaTemplate.send(topic, messageJson).whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("❌ Ошибка при отправке сообщения в Kafka (topic: {}): {}", topic, messageJson, ex);
                } else {
                    logger.info("✅ Сообщение успешно отправлено в Kafka (topic: {}): {}", topic, messageJson);
                }
            });

        } catch (Exception e) {
            logger.error("❌ Ошибка сериализации сообщения для Kafka", e);
        }
    }
}
