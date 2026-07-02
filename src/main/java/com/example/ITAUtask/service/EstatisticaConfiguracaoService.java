package com.example.ITAUtask.service;

import com.example.ITAUtask.dto.EstatisticaIntervaloHistoricoResponse;
import com.example.ITAUtask.model.EstatisticaIntervalo;
import com.example.ITAUtask.repository.EstatisticaIntervaloRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class EstatisticaConfiguracaoService {

    private final EstatisticaIntervaloRepository repository;
    private final AtomicLong _intervaloInicial;

    public EstatisticaConfiguracaoService(
            EstatisticaIntervaloRepository repository,
            @Value("${estatistica.intervalo-segundos}") long intervaloInicial
    ) {
        this.repository = repository;
        this._intervaloInicial = new AtomicLong(intervaloInicial);
    }

    @PostConstruct
    public void inicializarIntervalo() {
        EstatisticaIntervalo intervaloAtual = repository.findFirstByAtivoTrue()
                .orElseGet(() -> repository.save(
                        new EstatisticaIntervalo(
                                _intervaloInicial.get(),
                                Instant.now(),
                                true
                        )
                ));

        log.info(
                "Intervalo de estatisticas inicializado com {} segundos",
                _intervaloInicial.get()
        );
    }

    public long buscarIntervaloSegundos() {
        EstatisticaIntervalo intervaloAtual = repository.findFirstByAtivoTrue()
                .orElseGet(() ->
                        new EstatisticaIntervalo(
                                _intervaloInicial.get(),
                                Instant.now(),
                                true
                        )
                );
        return intervaloAtual.getIntervaloSegundos();
    }

    public long atualizarIntervaloSegundos(long novoIntervaloSegundos) {
        repository.findFirstByAtivoTrue()
                .ifPresent(intervaloAtual -> {
                    intervaloAtual.desativar();
                    repository.save(intervaloAtual);
                });

        EstatisticaIntervalo novoIntervalo = repository.save(
                new EstatisticaIntervalo(
                        novoIntervaloSegundos,
                        Instant.now(),
                        true
                )
        );

        log.info(
                "Intervalo de estatisticas atualizado para {} segundos",
                novoIntervaloSegundos
        );

        return novoIntervalo.getIntervaloSegundos();
    }

    public List<EstatisticaIntervaloHistoricoResponse> listarHistorico() {
        return repository.findAllByOrderByDataHoraAlteracaoDesc()
                .stream()
                .map(intervalo -> new EstatisticaIntervaloHistoricoResponse(
                        intervalo.getIntervaloSegundos(),
                        intervalo.getDataHoraAlteracao(),
                        intervalo.isAtivo()
                ))
                .toList();
    }
}
