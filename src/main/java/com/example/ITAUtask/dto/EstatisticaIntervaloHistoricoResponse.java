package com.example.ITAUtask.dto;

import java.time.Instant;

public record EstatisticaIntervaloHistoricoResponse(
        long intervaloSegundos,
        Instant dataHoraAlteracao,
        boolean ativo
) {
}
