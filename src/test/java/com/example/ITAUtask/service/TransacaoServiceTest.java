package com.example.ITAUtask.service;

import com.example.ITAUtask.dto.TransacaoRequest;
import com.example.ITAUtask.dto.TransacaoResponse;
import com.example.ITAUtask.exception.TransacaoInvalidaException;
import com.example.ITAUtask.model.Transacao;
import com.example.ITAUtask.repository.EstatisticaIntervaloRepository;
import com.example.ITAUtask.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransacaoServiceTest {

    @Test
    void deveSalvarTransacaoValida() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        TransacaoService service =
                new TransacaoService(repository, configuracaoService);

        TransacaoRequest request =
                new TransacaoRequest(
                        BigDecimal.valueOf(100),
                        OffsetDateTime.now()
                );

        service.registrar(request);

        verify(repository).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoParaDataFutura() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        TransacaoService service =
                new TransacaoService(repository, configuracaoService);

        TransacaoRequest request =
                new TransacaoRequest(
                        BigDecimal.valueOf(100),
                        OffsetDateTime.now().plusMinutes(10)
                );

        assertThrows(
                TransacaoInvalidaException.class,
                () -> service.registrar(request)
        );

        verify(repository, never()).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoParaDataForaDoLimite() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        TransacaoService service =
                new TransacaoService(repository, configuracaoService);

        TransacaoRequest request =
                new TransacaoRequest(
                        BigDecimal.valueOf(100),
                        OffsetDateTime.now().minusSeconds(61)
                );

        assertThrows(
                TransacaoInvalidaException.class,
                () -> service.registrar(request)
        );

        verify(repository, never()).save(any(Transacao.class));
    }

    @Test
    void deveLimparTodasAsTransacoes() {

        TransacaoRepository repository =
                mock(TransacaoRepository.class);

        EstatisticaConfiguracaoService configuracaoService =
                criarConfiguracaoService(60L);

        TransacaoService service =
                new TransacaoService(repository, configuracaoService);

        service.limpar();

        verify(repository).deleteAll();
    }

    @Test
    void deveListarTodasAsTransacoesSemAplicarIntervalo() {

        TransacaoRepository repository = mock(TransacaoRepository.class);
        EstatisticaConfiguracaoService configuracaoService = criarConfiguracaoService(60L);
        TransacaoService service = new TransacaoService(repository, configuracaoService);
        Transacao transacao = new Transacao(
                BigDecimal.valueOf(100),
                Instant.parse("2020-01-01T00:00:00Z")
        );

        when(repository.findAll()).thenReturn(List.of(transacao));

        List<TransacaoResponse> transacoes = service.listar();

        assertEquals(1, transacoes.size());
        assertEquals(BigDecimal.valueOf(100), transacoes.getFirst().valor());
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), transacoes.getFirst().dataHora());
        verify(repository).findAll();
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
