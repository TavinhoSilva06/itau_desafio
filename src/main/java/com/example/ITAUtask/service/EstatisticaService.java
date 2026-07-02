package com.example.ITAUtask.service;

import com.example.ITAUtask.dto.EstatisticaResponse;
import com.example.ITAUtask.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstatisticaService {

    private final TransacaoRepository repository;
    private final EstatisticaConfiguracaoService configuracaoService;

    public EstatisticaResponse calcular() {

        // Antes de iniciar o cálculo Marca o tempo inicial de execução em milissegundos
        long inicio = System.currentTimeMillis();

        log.info(
                "Iniciando cálculo das estatísticas"
        );

        Instant limite = Instant.now()
                        // Pega o horário atual
                        .minusSeconds(configuracaoService.buscarIntervaloSegundos());
        // Subtrai a quantidade de segundos definida no application.properties
        // Exemplo:
        // intervaloSegundos = X segundos, pois você pode alterar no properties quando quiser



        DoubleSummaryStatistics stats = repository.findAll()
                // Busca todas as transações salvas no repository

                .stream()
                // Transforma a lista em Stream para poder usar operações funcionais

                .filter(t -> t.getDataHora().isAfter(limite)
                )
                /*
                    FILTRO PRINCIPAL DA REGRA DE NEGÓCIO

                    Aqui verificamos se a transação aconteceu
                    DEPOIS do horário limite.

                    Exemplo:

                    limite = 22:29:40

                    Transação 1:
                    22:30:10
                    -> isAfter(limite) = TRUE
                    -> entra no cálculo

                    Transação 2:
                    22:28:00
                    -> isAfter(limite) = FALSE
                    -> ignorada

                    Em resumo:
                    Apenas transações dos últimos X segundos
                    serão usadas nas estatísticas.
                 */

                .mapToDouble(t -> t.getValor().doubleValue())
                /*
                    Pega apenas o valor monetário da transação
                    e converte para double.

                    Exemplo:
                    BigDecimal(100.50)
                    ->
                    100.50
                 */

                .summaryStatistics();
        /*
            Calcula automaticamente:

            - quantidade (count)
            - soma (sum)
            - média (average)
            - menor valor (min)
            - maior valor (max)
         */

        // Marca o tempo final de execução em milissegundos
        long fim = System.currentTimeMillis();

        // Registra em um Log
        log.info(
                "Cálculo executado em {} ms",
                fim - inicio
        );

        if (stats.getCount() == 0) {
            return new EstatisticaResponse(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        return new EstatisticaResponse(
                stats.getCount(),
                BigDecimal.valueOf(stats.getSum()),
                BigDecimal.valueOf(stats.getAverage())
                        .setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(stats.getMin()),
                BigDecimal.valueOf(stats.getMax())
        );
    }
}
