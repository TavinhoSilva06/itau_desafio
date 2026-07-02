package com.example.ITAUtask.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "transacoes")
public class Transacao {

    @Id
    private String id;

    private BigDecimal valor;

    private Instant dataHora;

    public Transacao(
            BigDecimal valor,
            Instant dataHora
    ) {
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Instant getDataHora() {
        return dataHora;
    }
}