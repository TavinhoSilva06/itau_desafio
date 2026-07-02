package com.example.ITAUtask.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "estatistica_intervalos")
public class EstatisticaIntervalo {

    @Id
    private String id;

    private long intervaloSegundos;

    private Instant dataHoraAlteracao;

    private boolean ativo;

    public EstatisticaIntervalo(
            long intervaloSegundos,
            Instant dataHoraAlteracao,
            boolean ativo
    ) {
        this.intervaloSegundos = intervaloSegundos;
        this.dataHoraAlteracao = dataHoraAlteracao;
        this.ativo = ativo;
    }

    public String getId() {
        return id;
    }

    public long getIntervaloSegundos() {
        return intervaloSegundos;
    }

    public Instant getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void desativar() {
        this.ativo = false;
    }
}
