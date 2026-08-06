package com.example.ITAUtask.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransacaoResponse(
        String id,
        BigDecimal valor,
        Instant dataHora
) {
}
