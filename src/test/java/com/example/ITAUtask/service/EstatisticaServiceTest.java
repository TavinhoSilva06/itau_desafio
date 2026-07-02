package com.example.ITAUtask.service;

import com.example.ITAUtask.dto.EstatisticaResponse;
import com.example.ITAUtask.model.Transacao;
import com.example.ITAUtask.repository.EstatisticaIntervaloRepository;
import com.example.ITAUtask.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstatisticaServiceTest {

    @Test
    void deveRetornarZerosQuandoNaoExistemTransacoes() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        when(repository.findAll())
                .thenReturn(List.of());

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        EstatisticaService service =
                new EstatisticaService(repository, configuracaoService);

        EstatisticaResponse response =
                service.calcular();

        assertEquals(0, response.count());
        assertEquals(0, response.sum().doubleValue());
        assertEquals(0, response.avg().doubleValue());
        assertEquals(0, response.min().doubleValue());
        assertEquals(0, response.max().doubleValue());
    }

    @Test
    void deveCalcularEstatisticasCorretamente() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        when(repository.findAll())
                .thenReturn(List.of(
                        new Transacao(
                                BigDecimal.valueOf(100),
                                Instant.now()
                        ),
                        new Transacao(
                                BigDecimal.valueOf(200),
                                Instant.now()
                        ),
                        new Transacao(
                                BigDecimal.valueOf(300),
                                Instant.now()
                        )
                ));

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        EstatisticaService service =
                new EstatisticaService(repository, configuracaoService);

        EstatisticaResponse response =
                service.calcular();

        assertEquals(3, response.count());
        assertEquals(600.0, response.sum().doubleValue());
        assertEquals(200.0, response.avg().doubleValue());
        assertEquals(100.0, response.min().doubleValue());
        assertEquals(300.0, response.max().doubleValue());
    }

    @Test
    void deveIgnorarTransacoesForaDaJanelaDeTempo() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        when(repository.findAll())
                .thenReturn(List.of(
                        new Transacao(
                                BigDecimal.valueOf(100),
                                Instant.now()
                        ),
                        new Transacao(
                                BigDecimal.valueOf(200),
                                Instant.now().minusSeconds(120)
                        )
                ));

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        EstatisticaService service =
                new EstatisticaService(repository, configuracaoService);

        EstatisticaResponse response =
                service.calcular();

        assertEquals(1, response.count());
        assertEquals(100.0, response.sum().doubleValue());
        assertEquals(100.0, response.avg().doubleValue());
        assertEquals(100.0, response.min().doubleValue());
        assertEquals(100.0, response.max().doubleValue());
    }

    @Test
    void deveUsarIntervaloAtualizadoDinamicamente() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        when(repository.findAll())
                .thenReturn(List.of(
                        new Transacao(
                                BigDecimal.valueOf(100),
                                Instant.now().minusSeconds(90)
                        )
                ));

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        EstatisticaService service =
                new EstatisticaService(repository, configuracaoService);

        assertEquals(0, service.calcular().count());

        configuracaoService.atualizarIntervaloSegundos(120L);

        assertEquals(1, service.calcular().count());
    }

    private EstatisticaConfiguracaoService criarConfiguracaoService(
            long intervaloInicial
    ) {
        EstatisticaIntervaloRepository repository =
                mock(EstatisticaIntervaloRepository.class);

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return new EstatisticaConfiguracaoService(
                repository,
                intervaloInicial
        );
    }
}
