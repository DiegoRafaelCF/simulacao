package com.hack.simulacao.domain.h2;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "TELEMETRIA")
@Data
public class Telemetria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ENDPOINT")
    private String endpoint;

    @Column(name = "STATUS_CODE")
    private Integer statusCode;

    @Column(name = "TEMPO_MS")
    private Long tempoMs;

    @CreationTimestamp
    @Column(name = "DATA_EVENTO", updatable = false)
    private LocalDateTime dataEvento;
}
