package com.hack.simulacao.infra.messaging;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hack.simulacao.domain.Simulacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventHubPublisher {

    private final EventHubProducerClient producerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventHubPublisher(
            @Value("${eventhub.connection-string}") String connectionString,
            @Value("${eventhub.topic}") String topic) {
        this.producerClient = new EventHubClientBuilder()
                .connectionString(connectionString, topic)
                .buildProducerClient();
    }

    public void publish(Simulacao simulacao) {
        try {
            String payload = objectMapper.writeValueAsString(simulacao);

            EventDataBatch batch = producerClient.createBatch();
            batch.tryAdd(new com.azure.messaging.eventhubs.EventData(payload));

            producerClient.send(batch);
        } catch (Exception e) {
            System.err.println("Erro ao publicar no EventHub: " + e.getMessage());
        }
    }
}
