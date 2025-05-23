package kz.spring.springboot.chat.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.spring.springboot.chat.Entity.Message;
import kz.spring.springboot.chat.Repository.MessageRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;

    public KafkaMessageConsumer(ObjectMapper objectMapper, MessageRepository messageRepository) {
        this.objectMapper = objectMapper;
        this.messageRepository = messageRepository;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-consumer")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String messageJson = record.value();
            // если хочешь — можно раскомментировать для дебага
            // logger.debug("📨 JSON из Kafka: {}", messageJson);

            Message message = objectMapper.readValue(messageJson, Message.class);
            messageRepository.save(message);

            // Логируем в человекочитаемом виде, сделай toString() в Message информативным
            logger.info("📥 Получено и сохранено сообщение из Kafka: {}", message);

        } catch (Exception e) {
            logger.error("❌ Ошибка при обработке сообщения из Kafka", e);
        }
    }
}
