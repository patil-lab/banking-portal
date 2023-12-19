package com.example.bankingportal.config;

import com.example.bankingportal.entity.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class KafkaProducerConfig {

    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopic;

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerConfig.class);
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Transaction message) {

        var future = kafkaTemplate.send(kafkaTopic,message);

        future.whenComplete((sendResult, exception) -> {
            if (exception != null) {
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            LOGGER.info("Transaction sent  to Kafka topic : "+ message);
        });
    }
}
