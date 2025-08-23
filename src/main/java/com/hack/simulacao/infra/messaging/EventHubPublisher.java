package com.hack.simulacao.infra.messaging;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hack.simulacao.domain.h2.Simulacao;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventHubPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventHubPublisher.class);

    @Value("${azure.eventhubs.connection-string}")
    private String connectionString;

    @Value("${azure.eventhubs.name}")
    private String eventHubName;

    private EventHubProducerClient producerClient;
    private final ObjectMapper objectMapper;

    public EventHubPublisher() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void init() {
        if(connectionString == null || eventHubName == null) {
            logger.error("As variaveis de ambiente 'azure.eventhubs.connection-string' e 'azure.eventhubs.name' não estão configuradas corretamente.");
        }
        try {
            this.producerClient = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();
            logger.info("EventHubProducerClient inicializado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao inicializar EventHubProducerClient: {}", e.getMessage());
        }
    }

    public void publish(Simulacao simulacao) {
        if (producerClient == null) {
            logger.error("Cliente do EventHub não está inicializado. Falha ao publicar evento.");
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(simulacao);

            EventDataBatch batch = producerClient.createBatch();
            batch.tryAdd(new EventData(payload));

            producerClient.send(batch);
            logger.info("Evento publicado no EventHub: {}", payload);
        } catch (Exception e) {
            logger.error("Erro ao serializar objeto para JSON ou ao enviar para o EventHub", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if(producerClient != null) {
            producerClient.close();
            logger.info("EventHubProducerClient fechado com sucesso");
        }
    }
}
