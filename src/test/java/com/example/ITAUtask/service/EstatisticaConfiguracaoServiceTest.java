package com.example.ITAUtask.service;

import com.example.ITAUtask.dto.EstatisticaIntervaloHistoricoResponse;
import com.example.ITAUtask.model.EstatisticaIntervalo;
import com.example.ITAUtask.repository.EstatisticaIntervaloRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EstatisticaConfiguracaoServiceTest {

    @Test
    void deveCriarIntervaloInicialQuandoNaoExistirAtivo() {

        EstatisticaIntervaloRepository repository =
                mock(EstatisticaIntervaloRepository.class);

        when(repository.findFirstByAtivoTrue())
                .thenReturn(Optional.empty());

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EstatisticaConfiguracaoService service =
                new EstatisticaConfiguracaoService(repository, 3600L);

        service.inicializarIntervalo();

        assertEquals(3600L, service.buscarIntervaloSegundos());

        ArgumentCaptor<EstatisticaIntervalo> captor =
                ArgumentCaptor.forClass(EstatisticaIntervalo.class);

        verify(repository).save(captor.capture());

        assertEquals(3600L, captor.getValue().getIntervaloSegundos());
        assertEquals(true, captor.getValue().isAtivo());
    }

    @Test
    void deveUsarIntervaloAtivoExistente() {

        EstatisticaIntervaloRepository repository =
                mock(EstatisticaIntervaloRepository.class);

        when(repository.findFirstByAtivoTrue())
                .thenReturn(Optional.of(
                        new EstatisticaIntervalo(
                                120L,
                                Instant.now(),
                                true
                        )
                ));

        EstatisticaConfiguracaoService service =
                new EstatisticaConfiguracaoService(repository, 3600L);

        service.inicializarIntervalo();

        assertEquals(120L, service.buscarIntervaloSegundos());
    }

    @Test
    void deveDesativarIntervaloAnteriorECriarNovoAtivo() {

        EstatisticaIntervaloRepository repository =
                mock(EstatisticaIntervaloRepository.class);

        EstatisticaIntervalo intervaloAnterior =
                new EstatisticaIntervalo(
                        60L,
                        Instant.now().minusSeconds(60),
                        true
                );

        when(repository.findFirstByAtivoTrue())
                .thenReturn(Optional.of(intervaloAnterior));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EstatisticaConfiguracaoService service =
                new EstatisticaConfiguracaoService(repository, 60L);

        long intervaloAtualizado =
                service.atualizarIntervaloSegundos(120L);

        assertEquals(120L, intervaloAtualizado);
        assertEquals(120L, service.buscarIntervaloSegundos());
        assertFalse(intervaloAnterior.isAtivo());

        verify(repository, times(2)).save(any(EstatisticaIntervalo.class));
    }

    @Test
    void deveListarHistoricoOrdenadoPeloRepository() {

        EstatisticaIntervaloRepository repository =
                mock(EstatisticaIntervaloRepository.class);

        when(repository.findAllByOrderByDataHoraAlteracaoDesc())
                .thenReturn(List.of(
                        new EstatisticaIntervalo(
                                120L,
                                Instant.parse("2026-06-25T03:10:00Z"),
                                true
                        ),
                        new EstatisticaIntervalo(
                                60L,
                                Instant.parse("2026-06-25T03:00:00Z"),
                                false
                        )
                ));

        EstatisticaConfiguracaoService service =
                new EstatisticaConfiguracaoService(repository, 60L);

        List<EstatisticaIntervaloHistoricoResponse> historico =
                service.listarHistorico();

        assertEquals(2, historico.size());
        assertEquals(120L, historico.get(0).intervaloSegundos());
        assertEquals(true, historico.get(0).ativo());
        assertEquals(60L, historico.get(1).intervaloSegundos());
        assertEquals(false, historico.get(1).ativo());
    }
}
